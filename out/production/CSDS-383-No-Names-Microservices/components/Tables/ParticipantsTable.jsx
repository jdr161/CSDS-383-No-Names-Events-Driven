import React, { Component } from "react"
import { Table, Thead, Tbody, TableContainer, Tr, Th, Td, Heading } from "@chakra-ui/react"

class ParticipantsTable extends Component {
    render() {
        return (
            <TableContainer maxHeight={'50vh'} overflowY={'auto'} overflowX={'auto'}>
                <Heading>Participants</Heading>
                <Table variant={'simple'}>
                    <Thead position={'sticky'} top={'0'} bgColor={'gray.100'}>
                        <Tr>
                            <Th>ID</Th>
                            <Th>Name</Th>
                            <Th>Email</Th>
                        </Tr>
                    </Thead>
                    <Tbody>
                        {this.props.participants.map((participant => {
                            return (
                                <Tr key={participant.id}>
                                    <Td>{participant.id}</Td>
                                    <Td>{participant.name}</Td>
                                    <Td>{participant.email}</Td>
                                </Tr>
                            )
                        }))}
                    </Tbody>
                </Table>
            </TableContainer>
        )
    }
}

export default ParticipantsTable
