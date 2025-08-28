package com.loopers.domain.platform;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataPlatformService {
    private final DataPlatformGateway dataPlatformGateway;

    public void send(PlatformCommand.Order command) {
        dataPlatformGateway.send(command);
    }

    public void send(List<PlatformCommand.Order> commands) {
        commands.forEach(dataPlatformGateway::send);
    }

    public void send(PlatformCommand.Payment command) {
        dataPlatformGateway.send(command);
    }
}
