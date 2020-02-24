package com.xkom.api;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.List;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class remove_dupes_script {

    public static void main(String[] args) throws IOException {
        String collect = List.ofAll(Files.readAllLines(Paths.get("C:\\Users\\Mat\\Desktop\\select___from_price_ORDER_BY_id_DESC.csv")))
                .map(it -> it.split(","))
                .map(arr -> Tuple.of(arr[0], arr[1], arr[2], arr[3]))
                .groupBy(tp -> tp._4)
                .mapValues(ls -> ls.map(tp -> Tuple.of(tp._1, BigDecimal.valueOf(Double.parseDouble(tp._2)), LocalDateTime.parse(tp._3.replace(" ", "T"))))
                        .sortBy(Tuple3::_3))
                .map(remove_dupes_script::extractSameIds)
                .values()
                .flatMap(List::ofAll)
                .toJavaStream().collect(Collectors.joining(","));
        System.out.print(collect);
    }

    private static Tuple2<String, List<String>> extractSameIds(String s, List<Tuple3<String, BigDecimal, LocalDateTime>> object) {
        List<Tuple2<LocalDateTime, BigDecimal>> distinctPrices = object.map(t -> Tuple.of(t._3, t._2)).distinctBy(Tuple2::_2);
        List<Tuple3<String, BigDecimal, LocalDateTime>> f = object.filter(tp -> distinctPrices.find(rec -> rec._1.equals(tp._3) && rec._2.equals(tp._2)).isEmpty());
        return Tuple.of(s, f.map(Tuple3::_1));
    }
}
