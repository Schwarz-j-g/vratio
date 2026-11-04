use std::sync::Mutex;

use crate::model::TransportPacket;


pub struct ServerState {
    pub buffer: Mutex<Vec<TransportPacket>>,
}

impl ServerState {
    pub fn new() -> Self {
        let packet = TransportPacket::default();

        let initial_buffer = vec![packet; 256];

        Self {
            buffer: Mutex::new(initial_buffer)
        }
    }
}