package com.suyogindia.model;

import java.util.List;

/**
 * Created by suyogcomputech on 17/10/16.
 */

public class ListCategoryResponse {
    private List<Category> categoryList;
    private String status;
    private String message;

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public class Category {
        String id, name,status;

        public String getStatus() {
            return status;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
