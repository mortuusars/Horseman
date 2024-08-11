package io.github.mortuusars.horse_please.test;

import com.mojang.datafixers.util.Pair;
import io.github.mortuusars.horse_please.HorsePlease;
import io.github.mortuusars.horse_please.test.framework.Test;
import io.github.mortuusars.horse_please.test.framework.TestResult;
import io.github.mortuusars.horse_please.test.framework.TestingResult;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tests {
    private final ServerPlayer player;

    public Tests(ServerPlayer player) {
        this.player = player;
    }

    public TestingResult run() {
        HorsePlease.LOGGER.info("RUNNING TESTS");

        Pair<List<TestResult>, List<TestResult>> ran = run(
            List.of(new Test("empty", pl -> {}))
        );

        List<TestResult> skipped = skip();
        TestingResult testingResult = new TestingResult(ran.getFirst(), ran.getSecond(), skipped);
        HorsePlease.LOGGER.info(String.join("",
                "TESTS COMPLETED!\n",
                testingResult.getTotalTestCount() + " test(s) were conducted.",
                !testingResult.passed().isEmpty() ? ("\nPassed:\n" + testingResult.passed().stream()
                        .map(TestResult::toString).collect(Collectors.joining("\n"))) : "",
                !testingResult.failed().isEmpty() ? ("\nFailed:\n" + testingResult.failed().stream()
                        .map(TestResult::toString).collect(Collectors.joining("\n"))) : "",
                !testingResult.skipped().isEmpty() ? ("\nSkipped:\n" + testingResult.skipped().stream()
                        .map(TestResult::toString).collect(Collectors.joining("\n"))) : ""));
        return testingResult;
    }

    @SafeVarargs
    private Pair<List<TestResult>, List<TestResult>> run(List<Test>... tests) {
        List<TestResult> passed = new ArrayList<>();
        List<TestResult> failed = new ArrayList<>();
        for (List<Test> list : tests) {
            for (Test test : list) {
                TestResult testResult = runTest(test);
                if (testResult.status() == TestResult.Status.PASSED)
                    passed.add(testResult);
                else
                    failed.add(testResult);
            }
        }
        return Pair.of(passed, failed);
    }

    @SafeVarargs
    private List<TestResult> skip(List<Test>... tests) {
        List<TestResult> results = new ArrayList<>();
        for (List<Test> list : tests) {
            for (Test test : list) {
                results.add(TestResult.skip(test.name));
            }
        }
        return results;
    }

    private TestResult runTest(Test test) {
        try {
            test.test.accept(player);
            return TestResult.pass(test.name);
        }
        catch (Exception e) {
            return TestResult.error(test.name, e.toString());
        }
    }
}
