package com.definesys.log.entity;

import com.definesys.log.processers.ProcessChain;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ：jenkin
 * @date ：Created at 2020/5/27 10:47
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Data
@AllArgsConstructor
public class Processer {
    private String name;
    private int order;
    private ProcessChain processer;

}
