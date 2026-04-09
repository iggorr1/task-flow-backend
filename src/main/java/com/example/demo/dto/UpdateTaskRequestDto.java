    package com.example.demo.dto;

    import java.util.Date;

    public class UpdateTaskRequestDto {
        private String title;
        private String description;

        public UpdateTaskRequestDto(String title, String description ) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }
