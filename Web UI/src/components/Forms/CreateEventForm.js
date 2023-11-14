import React, { Component } from "react"
import {
    FormControl,
    FormLabel,
    FormErrorMessage,
    Input,
    Button,
    Heading,
} from '@chakra-ui/react'
import moment from 'moment'
import axios from 'axios'
import { v4 as uuidv4 } from 'uuid'
import toast, { Toaster } from 'react-hot-toast'

class CreateEventForm extends Component {

    constructor() {
        super()
        this.state = {
            uuidInput: '',
            dateInput: '',
            timeInput: '',
            titleInput: '',
            descriptionInput: '',
            emailInput: '',
        }
        this.resetState = this.resetState.bind(this)
    }
    handleUuidChange = (e) => this.setState({ uuidInput: e.target.value })
    handleDateChange = (e) => this.setState({ dateInput: e.target.value })
    handleTimeChange = (e) => this.setState({ timeInput: e.target.value })
    handleTitleChange = (e) => this.setState({ titleInput: e.target.value })
    handleDescriptionChange = (e) => this.setState({ descriptionInput: e.target.value })
    handleEmailInput = (e) => this.setState({ emailInput: e.target.value })
    resetState = () => this.setState({
        uuidInput: '',
        dateInput: '',
        timeInput: '',
        titleInput: '',
        descriptionInput: '',
        emailInput: '',
    })

    render() {
        const emailRegex = new RegExp('^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$')

        const { uuidInput, dateInput, timeInput, titleInput, descriptionInput, emailInput } = this.state

        const isDateError = Boolean(dateInput === '')
        const isTimeError = Boolean(timeInput === '')
        const isTitleError = Boolean(titleInput === '' | titleInput.length > 255)
        const isDescriptionError = Boolean(descriptionInput === '' | descriptionInput.length > 600)
        const isEmailError = Boolean(emailInput === '' | !emailRegex.test(emailInput))

        const submitDisabled = Boolean(isDateError | isTimeError | isTitleError | isDescriptionError | isEmailError)

        const handleSubmit = () => {
            let data = {
                id: uuidInput,
                date: dateInput, //input with type 'date' is already in form "YYYY-MM-DD"
                time: moment(timeInput, 'HH:mm').format('hh:mm a'),
                title: titleInput,
                description: descriptionInput,
                hostEmail: emailInput,
            }
            if (data.id == '') {
                data.id = uuidv4()
            }
            let apiURL = `http://localhost:3001/api/create-event`
            axios.post(apiURL, data)
                .then(response => {
                    this.resetState()
                    const data = response.data
                    this.props.setEvents(this.props.events.concat([{
                        id: data.id,
                        date: data.date,
                        time: data.time,
                        title: data.title,
                        description: data.description,
                        hostEmail: data.hostEmail,
                        participants: []
                    }]))
                    toast.success("Event created successfully.")
                })
                .catch(function (error) {
                    console.log(error)
                    toast.error(error.response.data.message)
                });
        }

        return (
            <>
                <Toaster
                    position="bottom-right"
                    reverseOrder={false}
                />
                <div>
                    <Heading>Create Event</Heading>
                    <FormControl>
                        <FormLabel>UUID</FormLabel>
                        <Input type='text' value={uuidInput} onChange={this.handleUuidChange} placeholder="Set a UUID for the event, or leave blank for an auto-generated one..." />
                    </FormControl>

                    <FormControl isInvalid={isDateError}>
                        <FormLabel>Date</FormLabel>
                        <Input type='date' value={dateInput} onChange={this.handleDateChange} placeholder="Set a date for the event..." />
                        {isDateError &&
                            <FormErrorMessage>Date is required.</FormErrorMessage>
                        }
                    </FormControl>

                    <FormControl isInvalid={isTimeError}>
                        <FormLabel>Time</FormLabel>
                        <Input type='time' value={timeInput} onChange={this.handleTimeChange} placeholder="Set a time for the event..." />
                        {isTimeError &&
                            <FormErrorMessage>Time is required.</FormErrorMessage>
                        }
                    </FormControl>

                    <FormControl isInvalid={isTitleError}>
                        <FormLabel>Title</FormLabel>
                        <Input type='text' value={titleInput} onChange={this.handleTitleChange} placeholder="Set the title for the event..." />
                        {isTitleError &&
                            <FormErrorMessage>Title should be between 1 and 255 characters, inclusive.</FormErrorMessage>
                        }
                    </FormControl>

                    <FormControl isInvalid={isDescriptionError}>
                        <FormLabel>Description</FormLabel>
                        <Input type='text' value={descriptionInput} onChange={this.handleDescriptionChange} placeholder="Set the description for the event..." />
                        {isDescriptionError &&
                            <FormErrorMessage>Description should be between 1 and 600 characters, inclusive.</FormErrorMessage>
                        }
                    </FormControl>

                    <FormControl isInvalid={isEmailError}>
                        <FormLabel>Host Email</FormLabel>
                        <Input type='email' value={emailInput} onChange={this.handleEmailInput} placeholder="Enter the email of the event host..." />
                        {isEmailError &&
                            <FormErrorMessage>Invalid email.</FormErrorMessage>
                        }
                    </FormControl>
                    <Button onClick={handleSubmit} isDisabled={submitDisabled}>Submit</Button>
                </div>
            </>
        )
    }
} export default CreateEventForm