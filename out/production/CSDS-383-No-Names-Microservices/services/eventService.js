import axios from 'axios'

const getAllEvents = async () => {
    const response = await axios.get("/api/view-events")
    return response.data
}

const eventService = {
    getAllEvents
}
export default eventService