package com.jin.pixhive_backend.manage.websocket.disruptor;

import com.jin.pixhive_backend.manage.websocket.model.PictureEditRequestMessage;
import com.jin.pixhive_backend.model.entity.User;
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
