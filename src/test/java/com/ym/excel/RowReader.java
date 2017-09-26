package com.ym.excel;

import java.util.List;

/**
 * @Author yangmeng44
 * @Date 2017/7/20
 */
public class RowReader implements IRowReader{


    /* 业务逻辑实现方法
     * @see com.eprosun.util.excel.IRowReader#getRows(int, int, java.util.List)
     */
    public void getRows(int sheetIndex, int curRow, List<String> rowlist) {
        // TODO Auto-generated method stub
        System.out.print(curRow+" ");
        for (int i = 0; i < rowlist.size(); i++) {
            System.out.print(rowlist.get(i) + " ");
        }
        System.out.println();
    }

}
