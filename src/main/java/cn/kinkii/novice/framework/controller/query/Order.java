package cn.kinkii.novice.framework.controller.query;

import lombok.Data;
import lombok.NonNull;

@Data
public class Order {

    @NonNull
    private String column;

    @NonNull
    private Direction direction;

}
