use std::sync::Arc;

use axum::{Router, routing::{get, post}};

use crate::{handler::{pull_app, pull_client, push_data}, state::ServerState};


pub fn build_app() -> Router {
    let shared_state = Arc::new(ServerState::new());

    Router::new()
        .route("/api/v1/push", post(push_data))
        .route("/api/v1/pull/client", get(pull_client))
        .route("/api/v1/pull/app", get(pull_app))
        .with_state(shared_state)
}