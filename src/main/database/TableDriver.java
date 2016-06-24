package main.database;

import java.util.Collection;

import main.interfaces.Parametric;

abstract class TableDriver {

    abstract int size();
    
    abstract Collection<Parametric> select();
    abstract Collection<Parametric> selectPage(int page, int pageSize);
    
    abstract void insert(Collection<Parametric> collection);
    abstract void insertIgnore(Collection<Parametric> collection);
}