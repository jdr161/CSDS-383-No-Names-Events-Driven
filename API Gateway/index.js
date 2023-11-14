const { default: axios } = require('axios')
const express = require('express')
const cors = require('cors')

const app = express()
const port = 3001
const participantsMSPort = 8081
const eventsMSPort = 8080


// we are only sending json requests
app.use(express.json())

// CORS policy required to have browser accept data
app.use(cors());

app.get('/api/view-events', (req, res) => {
    let apiURL = `http://localhost:${eventsMSPort}/api/view-events`
    axios.get(apiURL).then(function (response){
        res.status(response.status).send(response.data)
    }).catch(function (error){
        console.log(error)
        if(error.code == 'ECONNREFUSED'){
            res.status(504).send({message: "Could not connect to event microservice"})
        } else {
            res.status(error.response.status).send(error.response.data)
        }  
    })
})

app.post('/api/create-event', (req, res) => {
    let apiURL = `http://localhost:${eventsMSPort}/api/create-event`

    axios.post(apiURL, req.body).then(function (response){
        res.status(response.status).send(response.data)
    }).catch(function (error){
        console.log(error)
        if(error.code == 'ECONNREFUSED'){
            res.status(504).send({message: "Could not connect to event microservice"})
        } else {
            res.status(error.response.status).send(error.response.data)
        }  
    })
})

app.get('/api/view-participants', (req, res) => {
    let apiURL = `http://localhost:${participantsMSPort}/api/view-participants`

    axios.get(apiURL, req.body).then(function (response){
        res.status(response.status).send(response.data)
    }).catch(function (error){
        console.log(error)
        if(error.code == 'ECONNREFUSED'){
            res.status(504).send({message: "Could not connect to participant microservice"})
        } else {
            res.status(error.response.status).send(error.response.data)
        }  
    })
})

app.post('/api/create-participant', (req, res) => {
    let apiURL = `http://localhost:${participantsMSPort}/api/create-participant`

    axios.post(apiURL, req.body).then(function (response){
        res.status(response.status).send(response.data)
    }).catch(function (error){
        console.log(error)
        if(error.code == 'ECONNREFUSED'){
            res.status(504).send({message: "Could not connect to participant microservice"})
        } else {
            res.status(error.response.status).send(error.response.data)
        }  
    })
})

app.put('/api/register-participant', (req, res) => {
    let participantId = req.query.participantId
    let eventId = req.query.eventId

    let apiURL = `http://localhost:${eventsMSPort}/api/register-participant?participantId=${participantId}&eventId=${eventId}`

    // putting trivial body in PUT request since it seems like the request is rejected for empty body
    axios.put(apiURL, {"empty": "body"}).then(function (response){
        res.status(response.status).send(response.data)
    }).catch(function (error){
        console.log(error)
        if(error.code == 'ECONNREFUSED'){
            res.status(504).send({message: "Could not connect to event microservice"})
        } else {
            res.status(error.response.status).send(error.response.data)
        }  
    })
})



app.listen(port, () => {
    console.log(`API Gateway listening at http://localhost:${port}`)
})