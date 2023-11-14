import { Table, Thead, Tbody, TableContainer, Tr, Th, Td, UnorderedList, ListItem, Heading } from "@chakra-ui/react"
import React, { Component } from "react"

class EventsTable extends Component {
    render() {
        return (
            <TableContainer maxHeight={'50vh'} overflowY={'auto'} overflowX={'auto'}>
                <Heading>Events</Heading>
                <Table variant={'simple'}>
                    <Thead position={'sticky'} top={'0'} bgColor={'gray.100'}>
                        <Tr>
                            <Th>ID</Th>
                            <Th>Date</Th>
                            <Th>Time</Th>
                            <Th>Title</Th>
                            <Th>Description</Th>
                            <Th>Host Email</Th>
                            <Th>Participants</Th>
                        </Tr>
                    </Thead>
                    <Tbody>
                        {this.props.events.map((event => {
                            return (
                                <Tr key={event.id}>
                                    <Td>{event.id}</Td>
                                    <Td>{event.date}</Td>
                                    <Td>{event.time}</Td>
                                    <Td>{event.title}</Td>
                                    <Td>{event.description}</Td>
                                    <Td>{event.hostEmail}</Td>
                                    <Td>{event.participants.map(participant => {
                                        return (
                                            <UnorderedList key={participant.id}>
                                                <ListItem key={participant.name}>Name: {participant.name}
                                                    <UnorderedList>
                                                        <ListItem key={participant.id}>ID: {participant.id}</ListItem>
                                                        <ListItem key={participant.email}>Email: {participant.email}</ListItem>
                                                    </UnorderedList>
                                                </ListItem>
                                            </UnorderedList>
                                        )
                                    })}</Td>
                                </Tr>
                            )
                        }))}
                    </Tbody>
                </Table>
            </TableContainer>
        )
    }
} 

export default EventsTable
