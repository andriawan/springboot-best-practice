{
  description = "Java, Maven, and Custom Rootless Docker Environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
  };

  outputs =
    { self, nixpkgs }:
    let
      supportedSystems = [
        "x86_64-linux"
        "aarch64-linux"
      ];
      forEachSystem = f: nixpkgs.lib.genAttrs supportedSystems (system: f system);
    in
    {
      devShells = forEachSystem (
        system:
        let
          pkgs = import nixpkgs { inherit system; };
        in
        {
          default = pkgs.mkShell {
            buildInputs = [
              pkgs.openjdk25
              pkgs.maven
              pkgs.docker # CLI & Engine
              pkgs.rootlesskit # Namespace virtualization
            ];
            shellHook = ''
              # 1. Path Configuration
              export XDG_RUNTIME_DIR="/tmp/docker-rt-$USER-springboot"
              export XDG_DATA_HOME="$HOME/.local/share/docker-springboot"
              export XDG_CONFIG_HOME="$HOME/.config/docker-springboot"

              mkdir -p "$XDG_RUNTIME_DIR" "$XDG_DATA_HOME" "$XDG_CONFIG_HOME"

              # 2. Wire Testcontainers directly to the active rootless socket
              export DOCKER_HOST="unix://$XDG_RUNTIME_DIR/docker.sock"
              export TESTCONTAINERS_HOST_OVERRIDE="127.0.0.1"
              export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE="$XDG_RUNTIME_DIR/docker.sock"
              export TESTCONTAINERS_RYUK_DISABLED=true

              # 3. FIX: Force Testcontainers to use a modern API version compatible with Docker 29+
              export COMPOSE_API_VERSION="1.45"
              export DOCKER_API_VERSION="1.45"

              # 4. Start the Rootless Daemon
              dockerd-rootless \
                --data-root "$XDG_DATA_HOME/docker" \
                --exec-root "$XDG_RUNTIME_DIR/docker" &

              DOCKER_PID=$!

              echo "☕ Nix Flake loaded with Java 25 & Background Rootless Docker!"
              echo "🐋 API Version Overridden to 1.45 for Testcontainers compatibility"

              # 5. Handle graceful termination
              cleanup() { echo -e "\nShutting down Docker daemon (PID: $DOCKER_PID)..."
                kill "$DOCKER_PID" 2>/dev/null
                wait "$DOCKER_PID" 2>/dev/null
                rm -rf "$XDG_RUNTIME_DIR"
                echo "Cleaned up temporary Docker sockets from /tmp. Goodbye!"
              }
              trap cleanup EXIT
            '';

          };
        }
      );
    };
}
