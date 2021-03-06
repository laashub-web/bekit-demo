/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2020-01-06 22:49 创建
 */
package demo.utils;

import demo.enums.ResultStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * 账务系统远程mock
 */
@Slf4j
public class AccountMock {
    private static final Random RANDOM = new Random();

    //上账
    public static ResultStatus upAccount(String acountId, long amount) throws TimeoutException {
        return mockInvoke();
    }

    // 下账
    public static ResultStatus downAccount(String acountId, long amount) throws TimeoutException {
        return mockInvoke();
    }

    private static ResultStatus mockInvoke() throws TimeoutException {
        // 这里以随机数模拟调用账务系统的结果
        int i = RANDOM.nextInt(100);
        if (i < 80) {
            log.info("模拟调用账务系统80%概率返回成功");
            return ResultStatus.SUCCESS;
        } else if (i < 90) {
            log.info("模拟调用账务系统10%概率返回失败");
            return ResultStatus.FAIL;
        } else if (i < 95) {
            log.info("模拟调用账务系统5%概率返回处理中");
            return ResultStatus.PROCESSING;
        } else {
            log.info("模拟调用账务系统5%概率调用超时");
            throw new TimeoutException("模拟调用账务系统5%概率调用超时");
        }
    }
}
