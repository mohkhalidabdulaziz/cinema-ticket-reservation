package com.soapassignmnet.WebService_JG0GNZ.entity;

import java.util.Objects;

public class SeatKey {
    private String row;
    private String column;
    public void setRow(String row) {
        this.row = row;
    }

    public void setColumn(String column) {
        this.column = column;
    }


    public SeatKey(String row, String column) {
        this.row = row;
        this.column = column;
    }

    public String getRow() {
        return row;
    }

    public String getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatKey seatKey = (SeatKey) o;
        return Objects.equals(row, seatKey.row) &&
                Objects.equals(column, seatKey.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}


