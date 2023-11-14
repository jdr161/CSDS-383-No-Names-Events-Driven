import axios from 'axios'

const getAllParticipants = async () => {
    const response = await axios.get("/api/view-participants")
    return response.data
}

const participantService = {
    getAllParticipants
}
export default participantService