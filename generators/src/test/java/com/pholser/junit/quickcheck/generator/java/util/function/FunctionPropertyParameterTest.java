/*
 The MIT License

 Copyright (c) 2010-2020 Paul R. Holser, Jr.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.pholser.junit.quickcheck.generator.java.util.function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import org.junit.Test;
import org.junit.runner.RunWith;

public class FunctionPropertyParameterTest {
    @Test public void unresolvedTypes() {
        assertThat(testResult(Unresolved.class), isSuccessful());
    }

    @RunWith(JUnitQuickcheck.class)
    public static class Unresolved<A, R> {
        @Property public void typesAreOk(
            Function<? super A, ? extends R> f,
            A arg) {

            R result = f.apply(arg);
        }
    }

    @Test public void unresolvedArgType() {
        assertThat(testResult(UnresolvedArgType.class), isSuccessful());
    }

    public static class UnresolvedArgType<A> extends Unresolved<A, Integer> {
        @Property public void consistent(
            Function<? super A, Integer> f,
            A arg) {

            Integer result = f.apply(arg);

            for (int i = 0; i < 10000; ++i)
                assertEquals(result, f.apply(arg));
        }
    }

    @Test public void resolvedTypes() {
        assertThat(testResult(ResolvedTypes.class), isSuccessful());
    }

    public static class ResolvedTypes extends UnresolvedArgType<Date> {
    }

    @Test public void callingDefaultFunctionMethod() {
        assertThat(
            testResult(CallingDefaultFunctionMethod.class),
            isSuccessful());
    }

    @RunWith(JUnitQuickcheck.class)
    public static class CallingDefaultFunctionMethod {
        @Property public <V> void defaultMethods(
            Function<? super V, ? extends Date> first,
            Function<? super Date, Integer> second,
            V arg) {

            Date intermediate = first.apply(arg);
            Integer ultimate = second.apply(intermediate);

            assertEquals(ultimate, second.compose(first).apply(arg));
            assertEquals(ultimate, first.andThen(second).apply(arg));
        }
    }

    @Test public void lambdasArePureFunctions() {
        assertThat(testResult(PureFunctions.class), isSuccessful());
    }

    @RunWith(JUnitQuickcheck.class)
    public static class PureFunctions {
        @Property public void hold(Function<Integer, List<Long>> f) {
            List<Long> ell = f.apply(34);
            for (int i = 0; i < 10000; ++i)
                assertEquals(ell, f.apply(34));
        }
    }
}
