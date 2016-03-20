package com.king.animator;

import com.googlecode.totallylazy.collections.PersistentMap;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;

import java.util.*;
import java.util.stream.Collectors;

import static com.googlecode.totallylazy.collections.PersistentMap.constructors.map;

public class PointsRotator {

    List<Map<String, Point2D>> rotate(Group group)
    {
        return group.getChildren()
                    .stream()
                    .map(Node::getTranslateY)
                    .map(point -> {
                        //Map<String, Point2D> hash = new HashMap<>();
                        PersistentMap<String, Point2D> hash = map();
                        if (point == 0) {
                            PersistentMap<String, Point2D> fromhash = hash.insert("from", new Point2D(0, 240));
                            PersistentMap<String, Point2D> tohash = fromhash.insert("to", new Point2D(0, 180));
                            return tohash;
                        }
                        PersistentMap<String, Point2D> fromhash = hash.insert("from", new Point2D(0, point));
                        PersistentMap<String, Point2D> tohash = fromhash.insert("to", new Point2D(0, point - 60));

                        return tohash;
                    }).collect(Collectors.toList());
    }
}
