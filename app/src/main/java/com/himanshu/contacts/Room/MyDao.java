package com.himanshu.contacts.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyDao {

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user where name LIKE  '%' || :firstName  || '%' ")
    List<User> findByName(String firstName);

    @Query("SELECT * FROM user where id Like :idd")
    User findDetails(int idd);

    @Insert
    public void addUser(User user);

    @Update
    public void updateDetails(User user);

    @Delete
    void delete(User user);
}
