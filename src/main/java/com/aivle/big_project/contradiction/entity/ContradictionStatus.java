package com.aivle.big_project.contradiction.entity;

public enum ContradictionStatus {
    UNRESOLVED,
    RESOLVED,
    DELETED;

    public boolean canChangeTo(ContradictionStatus target) {
        return switch (this) {
            case UNRESOLVED ->
                    target == RESOLVED || target == DELETED;
            case RESOLVED ->
                    target == UNRESOLVED;
            case DELETED ->
                    target == UNRESOLVED;
        };
    }

}