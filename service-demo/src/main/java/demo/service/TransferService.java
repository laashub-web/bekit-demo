/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-08 21:41 创建
 */
package demo.service;

import demo.dao.TransferDao;
import demo.entity.Transfer;
import demo.enums.Status;
import demo.enums.TransferStatus;
import demo.exception.DemoException;
import demo.order.TransferOrder;
import demo.result.TransferResult;
import demo.utils.OID;
import org.bekit.flow.FlowEngine;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceAfter;
import org.bekit.service.annotation.service.ServiceBefore;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 转账交易服务
 */
@Service    // 注意此处的@Service和spring的@Service不是同一个，只是名字相同而已
public class TransferService {
    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    @Autowired
    private FlowEngine flowEngine;

    @Autowired
    private TransferDao transferDao;

    @ServiceBefore   // 服务前置处理（可以进行业务参数检查，比如校验账号存不存）
    public void before(ServiceContext<TransferOrder, TransferResult> serviceContext) {
        // 本方法执行时不会有事务开启
        logger.info("执行TransferService.serviceCheck");
    }

    @ServiceExecute     // 服务执行，真正开始执行业务（如果@Service的enableTx属性为true，则会开启事务）
    public void execute(ServiceContext<TransferOrder, TransferResult> serviceContext) {
        Transfer transfer = buildTransfer(serviceContext.getOrder());
        transferDao.save(transfer);

        transfer = flowEngine.execute("transferFlow", transfer);
        switch (transfer.getStatus()) {
            case SUCCESS:
                break;
            case FAIL:
                // 抛出异常，在服务监听器里对返回值进行设值
                throw new DemoException(Status.FAIL, "执行失败");
            default:
                throw new DemoException(Status.PROCESS, "处理中");
        }
    }

    @ServiceAfter // 服务后置处理（一般情况下用不到）
    public void after(ServiceContext<TransferOrder, TransferResult> serviceContext) {
        logger.info("执行TransferService.serviceAfter");
    }

    private Transfer buildTransfer(TransferOrder order) {
        Transfer transfer = new Transfer();
        transfer.setOrderNo(order.getOrderNo());
        transfer.setBizNo(OID.newId());
        transfer.setPayerAccountNo(order.getPayerAccountNo());
        transfer.setPayeeAccountNo(order.getPayeeAccountNo());
        transfer.setAmount(order.getAmount());
        transfer.setStatus(TransferStatus.DOWN_PAYER);

        return transfer;
    }
}
