package com.ym.excel;

import java.util.List;

/**
 * @Author yangmeng44
 * @Date 2017/7/20
 */
public interface IRowReader {

    /**业务逻辑实现方法
     * @param sheetIndex
     * @param curRow
     * @param rowlist
     */
    public  void getRows(int sheetIndex, int curRow, List<String> rowlist);
}
