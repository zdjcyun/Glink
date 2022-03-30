package com.zcloud.alone.network.entity;

import lombok.*;

/**
 * 设备定义
 * @author xiadaru
 */
@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class DeviceIdentity {

    private String productId;

    private String deviceId;

}

