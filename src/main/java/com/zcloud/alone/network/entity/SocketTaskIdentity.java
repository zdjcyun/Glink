package com.zcloud.alone.network.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class SocketTaskIdentity {
    private String socketId;
    //是否开启任务
    private boolean taskStatus;

}
