Expression expression = statement.toExpressionStmt()
        .orElseThrow(IllegalArgumentException::new)
        .getExpression();
        if (expression.isAssignExpr()) {
            return paramsList.contains(expression.asAssignExpr()
                .getTarget()
                .toString());
        }