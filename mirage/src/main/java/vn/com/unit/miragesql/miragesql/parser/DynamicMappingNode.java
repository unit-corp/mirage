/*******************************************************************************
 * Class        ：<DynamicMappingNode>
 * Created date ：2025/08/08
 * Lasted date  ：2025/08/08
 * Author       ：PhatLT
 * Change log   ：Initial version
 ******************************************************************************/
package vn.com.unit.miragesql.miragesql.parser;

import java.util.Collection;

import lombok.Getter;
import vn.com.unit.miragesql.miragesql.bean.BeanDescFactory;
import vn.com.unit.miragesql.miragesql.util.OgnlUtil;

/**
 * DynamicMappingNode is used to handle dynamic SQL mapping based on OGNL expressions. It evaluates the expression and processes the result,
 * which can be a collection of items or a single string. The results are then parsed into SQL nodes and added to the SQL context. This
 * allows for flexible SQL generation based on dynamic conditions or data.
 */
@Getter
public class DynamicMappingNode extends AbstractNode {

    private final String expression;
    private final Object parsedExpression;
    private final BeanDescFactory beanDescFactory;

    public DynamicMappingNode(String expression, BeanDescFactory beanDescFactory) {
        this.expression = expression;
        this.parsedExpression = OgnlUtil.parseExpression(expression);
        this.beanDescFactory = beanDescFactory;
    }

    @Override
    public void accept(SqlContext ctx) {
        Object result = OgnlUtil.getValue(parsedExpression, ctx);
        Node emptyNode = new SqlNode(" ");
        if (result instanceof Collection) {
            Collection<?> collection = (Collection<?>) result;
            if( collection.isEmpty()) {
                return; // No items to process
            }
            for (Object item : collection) {
                Node node = new SqlParserImpl(item.toString(), beanDescFactory).parse();
                emptyNode.accept(ctx);
                node.accept(ctx);
            }
        } else if (result instanceof String) {
            String str = (String) result;
            Node node = new SqlParserImpl(str, beanDescFactory).parse();
            emptyNode.accept(ctx);
            node.accept(ctx);
        } else {
            throw new IllegalArgumentException("Dynamic mapping expression must return a Collection or String, but got: " + result);
        }

    }
}
