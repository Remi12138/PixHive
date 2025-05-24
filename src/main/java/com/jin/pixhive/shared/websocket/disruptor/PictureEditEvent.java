package com.jin.pixhive.shared.websocket.disruptor;

import com.jin.pixhive.shared.websocket.model.PictureEditRequestMessage;
import com.jin.pixhive.domain.user.entity.User;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class PictureEditEvent {

    private PictureEditRequestMessage pictureEditRequestMessage;

    /**
     * current use's session
     */
    private WebSocketSession session;

    private User user;

    private Long pictureId;

}
