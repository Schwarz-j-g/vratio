use std::sync::Arc;

use axum::{Json, extract::State, http::StatusCode, response::IntoResponse};

use crate::{model::TransportPacket, state::ServerState};

pub async fn push_data(
    State(state): State<Arc<ServerState>>,
    Json(packet): Json<TransportPacket>,
) -> impl IntoResponse {
    let type_id = packet.payload[0] as usize;

    let mut buffer = match state.buffer.lock() {
        Ok(guard)  => guard,
        Err(_) => return (StatusCode::INTERNAL_SERVER_ERROR, "Lock poisoned") 
    };

    buffer[type_id] = packet;

    (StatusCode::OK, "Success")
}


pub async fn pull_client(
    State(state): State<Arc<ServerState>>
) -> Result<Json<Vec<TransportPacket>>, StatusCode> {
    let buffer = match state.buffer.lock() {
        Ok(guard) => guard,
        Err(_) => return Err(StatusCode::INTERNAL_SERVER_ERROR)
    };

    Ok(Json(buffer.clone()))
}

pub async fn pull_app(
    State(state): State<Arc<ServerState>>
) -> Result<Json<Vec<TransportPacket>>, StatusCode> {
    let mut buffer = match state.buffer.lock() {
        Ok(guard) => guard,
        Err(_) => return Err(StatusCode::INTERNAL_SERVER_ERROR)
    };


    let default_pack = TransportPacket::default();

    let fresh_blank_buffer = vec![default_pack; 256];
    let data = std::mem::replace(&mut *buffer, fresh_blank_buffer);
    
    Ok(Json(data))
}