package com.becb.processnewpoint.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
//@DynamoDBTable(tableName = "user_votes")
@Setter
@Getter
public class UserVotes {

    String userId;
    List<String> pointsVoted;

    public void addPointVoted(String pointVoted) {
        if (pointsVoted == null)
            pointsVoted = new ArrayList<>();
        pointsVoted.add(pointVoted);
    }

    public String getpointsVotedAsString() {
        StringBuilder pointsVotedStr = new StringBuilder();
        if (pointsVoted != null) {
            pointsVotedStr.append("{");
            pointsVoted.forEach(photo -> {
                pointsVotedStr.append(photo).append(",");
            });
            pointsVotedStr.append("}");
            pointsVotedStr.deleteCharAt(pointsVotedStr.length() - 2);
        }

        return pointsVotedStr.toString();
    }
}
