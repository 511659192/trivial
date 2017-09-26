package com.ym.guava;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * Created by yangm on 2017/8/27.
 */
public class TableTest {

    @Test
    public void testTable() {
        //create()：返回一个空的HashBasedTable
        Table<String, Integer, Integer> table = HashBasedTable.create();
        /**
         * put(R rowKey, C columnKey, V value)：
         * 在指定row和column处放入value值
         */
        table.put("A", 1, 100);
        table.put("A", 2, 101);
        table.put("B", 1, 200);
        table.put("B", 2, 201);
        /**
         *  contains(Object rowKey, Object columnKey)：
         *  Table中是否存在指定rowKey和columnKey的映射关系
         */
        boolean containsA3 = table.contains("A", 3); //false
        boolean containColumn2 = table.containsColumn(2); //true
        boolean containsRowA = table.containsRow("A"); //true
        boolean contains201 = table.containsValue(201); //true
        /**
         * remove(Object rowKey,Object columnKey)：
         * 删除Table中指定行列值的映射关系
         */
        table.remove("A", 2);
        /**
         * get(Object rowKey, Object columnKey)：
         * 获取Table中指定行列值的映射关系
         */
        table.get("B", 2);
        /**
         * column(C columnKey)：返回指定columnKey下的所有rowKey与value映射
         */
        Map<String, Integer> columnMap = table.column(2);
        /**
         * row(R rowKey)：返回指定rowKey下的所有columnKey与value映射
         */
        Map<Integer, Integer> rowMap = table.row("B");
        /**
         * 返回以Table.Cell<R, C, V>为元素的Set集合
         * 类似于Map.entrySet
         */
        Set<Table.Cell<String, Integer, Integer>> cells = table.cellSet();
        for (Table.Cell<String, Integer, Integer> cell : cells) {
            //获取cell的行值rowKey
            cell.getRowKey();
            //获取cell的列值columnKey
            cell.getColumnKey();
            //获取cell的值value
            cell.getValue();
        }
    }
}