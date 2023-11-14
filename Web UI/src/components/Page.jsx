import React, { Component } from "react"

import CreateEventForm from './Forms/CreateEventForm'
import CreateParticipantForm from './Forms/CreateParticipantForm'
import RegisterParticipantForm from './Forms/RegisterParticipantForm'
import EventsTable from './Tables/EventsTable'
import ParticipantsTable from './Tables/ParticipantsTable'
import { Container, Stack, HStack, Button } from "@chakra-ui/react"
import eventService from "../services/eventService"
import participantService from "../services/participantService"

class Page extends Component {
    constructor() {
        super()
        this.state = {
            showCreateEventForm: false,
            showCreateParticipantForm: false,
            showRegisterParticipantForm: false,
            showEventsTable: false,
            showParticipantsTable: false,
            events: [],
            participants: []
        }
        this.setEvents = this.setEvents.bind(this)
        this.setParticipants = this.setParticipants.bind(this)
    }

    async componentDidMount() {
        try {
            const eventsResponse = await eventService.getAllEvents()
            this.setState({
                ...this.state,
                events: eventsResponse
            })
        } catch (error) {
            console.error("Failed to get all events")
        }

        try {
            const participantsResponse = await participantService.getAllParticipants()
            this.setState({
                ...this.state,
                participants: participantsResponse
            })
        } catch (error) {
            console.error("Failed to get all participants")
        }
    }

    setEvents(events) {
        this.setState({
            ...this.state,
            events: events
        })
    }

    setParticipants(participants) {
        this.setState({
            ...this.state,
            participants: participants
        })
    }

    showComponent(name) {
        switch (name) {
            case "showCreateEventForm":
                this.setState({
                    showCreateEventForm: true,
                    showCreateParticipantForm: false,
                    showRegisterParticipantForm: false,
                })
                break
            case "showCreateParticipantForm":
                this.setState({
                    showCreateEventForm: false,
                    showCreateParticipantForm: true,
                    showRegisterParticipantForm: false,
                })
                break
            case "showRegisterParticipantForm":
                this.setState({
                    showCreateEventForm: false,
                    showCreateParticipantForm: false,
                    showRegisterParticipantForm: true,
                })
                break
            case "showEventsTable":
                this.setState({
                    showEventsTable: true,
                    showParticipantsTable: false
                })
                break
            case "showParticipantsTable":
                this.setState({
                    showEventsTable: false,
                    showParticipantsTable: true
                })
                break
            default:
                console.log("type somewhere")
                break
        }
    }
    render() {
        const { showCreateEventForm, showCreateParticipantForm, showRegisterParticipantForm, showEventsTable, showParticipantsTable } = this.state
        return (
            <Container maxWidth={'100%'} height={'100%'} display={'flex'} justifyContent={'center'} mt={8}>
                <Stack display={'flex'} maxHeight={'100%'} maxWidth={'100%'} justifyContent={'center'} p={8}>
                    <HStack display={'flex'} justifyContent={'center'}>
                        <Button onClick={() => this.showComponent("showCreateEventForm")}>Create Event</Button>
                        <Button onClick={() => this.showComponent("showCreateParticipantForm")}>Create Participant</Button>
                        <Button onClick={() => this.showComponent("showRegisterParticipantForm")}>Register Participant</Button>
                        <Button onClick={() => this.showComponent("showEventsTable")}>Show All Events</Button>
                        <Button onClick={() => this.showComponent("showParticipantsTable")}>Show All Participants</Button>
                    </HStack>
                    <Stack maxHeight={'100%'} p={4} spacing={8}>
                        {showEventsTable && <EventsTable events={this.state.events} />}
                        {showParticipantsTable && <ParticipantsTable participants={this.state.participants} />}
                        {showCreateEventForm && <CreateEventForm setEvents={this.setEvents} events={this.state.events} />}
                        {showCreateParticipantForm && <CreateParticipantForm setParticipants={this.setParticipants} participants={this.state.participants} />}
                        {showRegisterParticipantForm && <RegisterParticipantForm setEvents={this.setEvents} events={this.state.events} />}
                    </Stack>
                </Stack>
            </Container>
        )
    }
} export default Page