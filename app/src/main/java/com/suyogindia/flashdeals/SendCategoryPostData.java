package com.suyogindia.flashdeals;

import java.util.List;

/**
 * Created by suyogcomputech on 17/10/16.
 */

public class SendCategoryPostData {
    private String userId;
    private List<String> categoryIds;

    public SendCategoryPostData(String userId, List<String> categoryIds) {
        this.userId = userId;
        this.categoryIds = categoryIds;
    }
}
