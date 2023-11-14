import axios from 'axios'

const getAllParticipants = async () => {
    const response = await axios.get(`http://localhost:3001/api/view-participants`)
    return response.data
}

const participantService = {
    getAllParticipants
}
export default participantService