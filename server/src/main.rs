#![windows_subsystem = "windows"]
use std::net::SocketAddr;

use tokio::net::TcpListener;


mod model;
mod state;
mod handler;
mod route;

#[tokio::main]
async fn main() {
    let app = route::build_app();

    let port: u16 = std::env::var("PORT")
        .unwrap_or("8080".to_string())
        .parse()
        .expect("The PORT variable must be a valid number");

    let addr = SocketAddr::from(([0, 0, 0, 0], port));

    let listener = TcpListener::bind(addr).await
        .expect("Failed to bind to socket address");

    axum::serve(listener, app)
        .await.expect("Error during server operation");
}
