package dao;

import java.util.List;

public interface Dao<T> {
    Object get(String id);
    List<T> getAll();
    // 23.01.2021, David Zagoršek - po potrebi dodaj ostalo - insert, update, delete
}
