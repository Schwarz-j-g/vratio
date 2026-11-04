use serde::{Deserialize, Serialize};



#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct TransportPacket {
    pub version: u8,
    pub payload: [u8; 2]
}

impl Default for TransportPacket {
    fn default() -> Self {
        Self {
            version: 0,
            payload: [0u8; 2]
        }
    }
}