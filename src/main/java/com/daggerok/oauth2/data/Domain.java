package com.daggerok.oauth2.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Created by mak on 4/30/16.
 */
@Data
@Document
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Domain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id String id;
    @NonNull String content;
}
