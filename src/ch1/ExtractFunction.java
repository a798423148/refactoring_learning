package ch1;

import model.Invoice;
import model.Performance;
import model.Play;
import orgin.Statement;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

/**
 * 提炼函数
 */
public class ExtractFunction {

    /**
     * 获取账单
     *
     * @param invoice
     * @param plays
     *
     * @return
     */
    public String statement(Invoice invoice, Map<String, Play> plays) {
        var totalAmount = 0;
        var volumeCredits = 0;
        var result = "Statement for " + invoice.getCustomer();

        //todo
        final NumberFormat format = NumberFormat.getInstance();


        for (Performance perf : invoice.getPerformances()) {
            final Play play = plays.get(perf.getPlayID());
            var thisAmount = amountFor(perf, play);

            // 增加观众量积分
            volumeCredits += Math.max(perf.getAudience() - 30, 0);
            // 为喜剧观众提供额外积分
            if ("comedy".equals(play.getType())) {
                volumeCredits += Math.floor(perf.getAudience() / 5);
            }

            // 打印一行订单信息
            result += String.format("%s: $%s (%s seats) \n", play.getName(), format.format(thisAmount / 100), perf.getAudience());
            totalAmount += thisAmount;
        }
        result += String.format("Amount owed is %s \n", format.format(totalAmount / 100));
        result += String.format("You earned %s credits \n", volumeCredits);

        return result;
    }

    /**
     * 计算单场金额
     *
     * @param perf
     * @param play
     *
     * @return
     */
    private Double amountFor(Performance perf, Play play) {
        var thisAmount = 0D;

        switch (play.getType()) {
            case "tragedy":
                thisAmount = 40000;
                if (perf.getAudience() > 30) {
                    thisAmount += 1000 * (perf.getAudience() - 30);
                }
                break;

            case "comedy":
                thisAmount = 30000;
                if (perf.getAudience() > 20) {
                    thisAmount += 10000 + 500 * (perf.getAudience() - 20);
                }
                thisAmount += 300 * perf.getAudience();
                break;

            default:
                throw new RuntimeException("unknown type" + play.getType());
        }

        return thisAmount;
    }

    public static void main(String[] args) {
        Play hamlet = new Play("Hamlet", "tragedy");
        Play asLike = new Play("As You Like It", "comedy");
        Play othello = new Play("Othello", "tragedy");

        final Map<String, Play> plays = Map.of("hamlet", hamlet, "as-like", asLike, "othello", othello);


        final List<Performance> performances = List.of(new Performance("hamlet", 55), new Performance("as-like", 35), new Performance("othello", 40));
        final Invoice invoice = new Invoice("BigCo", performances);


        Statement statement = new Statement();
        final String result = statement.statement(invoice, plays);
        System.out.println(result);
    }

}
