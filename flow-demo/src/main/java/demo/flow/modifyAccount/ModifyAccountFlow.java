/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-07 23:36 创建
 */
package demo.flow.modifyAccount;

import demo.entity.ModifyAccount;
import demo.enums.ModifyAccountType;
import demo.enums.ResultStatus;
import org.bekit.flow.annotation.flow.EndNode;
import org.bekit.flow.annotation.flow.Flow;
import org.bekit.flow.annotation.flow.PhaseNode;
import org.bekit.flow.annotation.flow.StartNode;
import org.bekit.flow.engine.FlowContext;

/**
 * 修改账务流程
 */
@Flow
public class ModifyAccountFlow {

    @StartNode(processor = "modifyProcessor")
    public String modify(ResultStatus resultStatus, FlowContext<ModifyAccount> context) {
        ModifyAccount modifyAccount = context.getTarget();
        switch (resultStatus) {
            case SUCCESS:
                return "success";
            case FAIL:
                if (modifyAccount.getType() == ModifyAccountType.NORMAL) {
                    return "fail";
                } else {
                    // 如果是必须成功，则重新修改调用账务的订单号，重新执行（因为账务也实现的幂等性，必须的修改订单号，账务系统才会再次尝试修改用户的账）
                    return "generateRefOrderNo";
                }
            case PROCESS:
                return null;
            default:
                throw new RuntimeException("处理器返回结果不合法");
        }
    }

    // 注意：@WaitNode是等待类型节点
    // 等待节点特征：除非流程引擎执行的第一个节点是本节点，否则流程引擎在执行到这类节点时会自动中断流程
    @PhaseNode(processor = "generateRefOrderNoProcessor")
    public String generateRefOrderNo(ResultStatus resultStatus) {
        switch (resultStatus) {
            case SUCCESS:
                return "modify";
            default:
                throw new RuntimeException("处理器返回结果不合法");
        }
    }

    @EndNode
    public void success() {
    }

    @EndNode
    public void fail() {
    }
}
