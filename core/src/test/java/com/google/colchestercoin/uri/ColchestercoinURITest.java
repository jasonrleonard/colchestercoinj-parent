/*
 * Copyright 2012 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.google.colchestercoin.uri;

import com.google.colchestercoin.core.Address;
import com.google.colchestercoin.core.Utils;
import com.google.colchestercoin.params.MainNetParams;
import com.google.colchestercoin.params.TestNet3Params;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;


public class ColchestercoinURITest {
    private ColchestercoinURI testObject = null;

    private static final String MAINNET_GOOD_ADDRESS = "LQz2pJYaeqntA9BFB8rDX5AL2TTKGd5AuN";

    @Test
    public void testConvertToBitcoinURI() throws Exception {
        Address goodAddress = new Address(MainNetParams.get(), MAINNET_GOOD_ADDRESS);
        
        // simple example
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS + "?amount=12.34&label=Hello&message=AMessage", ColchestercoinURI.convertToBitcoinURI(goodAddress, Utils.toNanoCoins("12.34"), "Hello", "AMessage"));
        
        // example with spaces, ampersand and plus
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS + "?amount=12.34&label=Hello%20World&message=Mess%20%26%20age%20%2B%20hope", ColchestercoinURI.convertToBitcoinURI(goodAddress, Utils.toNanoCoins("12.34"), "Hello World", "Mess & age + hope"));

        // no amount, label present, message present
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS + "?label=Hello&message=glory", ColchestercoinURI.convertToBitcoinURI(goodAddress, null, "Hello", "glory"));
        
        // amount present, no label, message present
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS + "?amount=0.1&message=glory", ColchestercoinURI.convertToBitcoinURI(goodAddress, Utils.toNanoCoins("0.1"), null, "glory"));
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS + "?amount=0.1&message=glory", ColchestercoinURI.convertToBitcoinURI(goodAddress, Utils.toNanoCoins("0.1"), "", "glory"));

        // amount present, label present, no message
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS + "?amount=12.34&label=Hello", ColchestercoinURI.convertToBitcoinURI(goodAddress, Utils.toNanoCoins("12.34"), "Hello", null));
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS + "?amount=12.34&label=Hello", ColchestercoinURI.convertToBitcoinURI(goodAddress, Utils.toNanoCoins("12.34"), "Hello", ""));
              
        // amount present, no label, no message
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS + "?amount=1000", ColchestercoinURI.convertToBitcoinURI(goodAddress, Utils.toNanoCoins("1000"), null, null));
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS + "?amount=1000", ColchestercoinURI.convertToBitcoinURI(goodAddress, Utils.toNanoCoins("1000"), "", ""));
        
        // no amount, label present, no message
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS + "?label=Hello", ColchestercoinURI.convertToBitcoinURI(goodAddress, null, "Hello", null));
        
        // no amount, no label, message present
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS + "?message=Agatha", ColchestercoinURI.convertToBitcoinURI(goodAddress, null, null, "Agatha"));
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS + "?message=Agatha", ColchestercoinURI.convertToBitcoinURI(goodAddress, null, "", "Agatha"));
      
        // no amount, no label, no message
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS, ColchestercoinURI.convertToBitcoinURI(goodAddress, null, null, null));
        assertEquals("colchestercoin:" + MAINNET_GOOD_ADDRESS, ColchestercoinURI.convertToBitcoinURI(goodAddress, null, "", ""));
    }

    @Test
    public void testGood_Simple() throws ColchestercoinURIParseException {
        testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS);
        assertNotNull(testObject);
        assertNull("Unexpected amount", testObject.getAmount());
        assertNull("Unexpected label", testObject.getLabel());
        assertEquals("Unexpected label", 20, testObject.getAddress().getHash160().length);
    }

    /**
     * Test a broken URI (bad scheme)
     */
    @Test
    public void testBad_Scheme() {
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), "blimpcoin:" + MAINNET_GOOD_ADDRESS);
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
        }
    }

    /**
     * Test a broken URI (bad syntax)
     */
    @Test
    public void testBad_BadSyntax() {
        // Various illegal characters
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + "|" + MAINNET_GOOD_ADDRESS);
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("Bad URI syntax"));
        }

        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS + "\\");
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("Bad URI syntax"));
        }

        // Separator without field
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":");
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("Bad URI syntax"));
        }
    }

    /**
     * Test a broken URI (missing address)
     */
    @Test
    public void testBad_Address() {
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME);
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
        }
    }

    /**
     * Test a broken URI (bad address type)
     */
    @Test
    public void testBad_IncorrectAddressType() {
        try {
            testObject = new ColchestercoinURI(TestNet3Params.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS);
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("Bad address"));
        }
    }

    /**
     * Handles a simple amount
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     */
    @Test
    public void testGood_Amount() throws ColchestercoinURIParseException {
        // Test the decimal parsing
        testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                + "?amount=6543210.12345678");
        assertEquals("654321012345678", testObject.getAmount().toString());

        // Test the decimal parsing
        testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                + "?amount=.12345678");
        assertEquals("12345678", testObject.getAmount().toString());

        // Test the integer parsing
        testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                + "?amount=6543210");
        assertEquals("654321000000000", testObject.getAmount().toString());
    }

    /**
     * Handles a simple label
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     */
    @Test
    public void testGood_Label() throws ColchestercoinURIParseException {
        testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                + "?label=Hello%20World");
        assertEquals("Hello World", testObject.getLabel());
    }

    /**
     * Handles a simple label with an embedded ampersand and plus
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void testGood_LabelWithAmpersandAndPlus() throws Exception {
        String testString = "Hello Earth & Mars + Venus";
        String encodedLabel = ColchestercoinURI.encodeURLString(testString);
        testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS + "?label="
                + encodedLabel);
        assertEquals(testString, testObject.getLabel());
    }

    /**
     * Handles a Russian label (Unicode test)
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void testGood_LabelWithRussian() throws Exception {
        // Moscow in Russian in Cyrillic
        String moscowString = "\u041c\u043e\u0441\u043a\u0432\u0430";
        String encodedLabel = ColchestercoinURI.encodeURLString(moscowString);
        testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS + "?label="
                + encodedLabel);
        assertEquals(moscowString, testObject.getLabel());
    }

    /**
     * Handles a simple message
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     */
    @Test
    public void testGood_Message() throws ColchestercoinURIParseException {
        testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                + "?message=Hello%20World");
        assertEquals("Hello World", testObject.getMessage());
    }

    /**
     * Handles various well-formed combinations
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     */
    @Test
    public void testGood_Combinations() throws ColchestercoinURIParseException {
        testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                + "?amount=6543210&label=Hello%20World&message=Be%20well");
        assertEquals(
                "ColchestercoinURI['address'='LQz2pJYaeqntA9BFB8rDX5AL2TTKGd5AuN','amount'='654321000000000','label'='Hello World','message'='Be well']",
                testObject.toString());
    }

    /**
     * Handles a badly formatted amount field
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     */
    @Test
    public void testBad_Amount() throws ColchestercoinURIParseException {
        // Missing
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                    + "?amount=");
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("amount"));
        }

        // Non-decimal (BIP 21)
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                    + "?amount=12X4");
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("amount"));
        }
    }

    /**
     * Handles a badly formatted label field
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     */
    @Test
    public void testBad_Label() throws ColchestercoinURIParseException {
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                    + "?label=");
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("label"));
        }
    }

    /**
     * Handles a badly formatted message field
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     */
    @Test
    public void testBad_Message() throws ColchestercoinURIParseException {
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                    + "?message=");
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("message"));
        }
    }

    /**
     * Handles duplicated fields (sneaky address overwrite attack)
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     */
    @Test
    public void testBad_Duplicated() throws ColchestercoinURIParseException {
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                    + "?address=aardvark");
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("address"));
        }
    }

    /**
     * Handles case when there are too many equals
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     */
    @Test
    public void testBad_TooManyEquals() throws ColchestercoinURIParseException {
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                    + "?label=aardvark=zebra");
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("cannot parse name value pair"));
        }
    }

    /**
     * Handles case when there are too many question marks
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     */
    @Test
    public void testBad_TooManyQuestionMarks() throws ColchestercoinURIParseException {
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                    + "?label=aardvark?message=zebra");
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("Too many question marks"));
        }
    }
    
    /**
     * Handles unknown fields (required and not required)
     * 
     * @throws ColchestercoinURIParseException
     *             If something goes wrong
     */
    @Test
    public void testUnknown() throws ColchestercoinURIParseException {
        // Unknown not required field
        testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                + "?aardvark=true");
        assertEquals("ColchestercoinURI['address'='LQz2pJYaeqntA9BFB8rDX5AL2TTKGd5AuN','aardvark'='true']", testObject.toString());

        assertEquals("true", (String) testObject.getParameterByName("aardvark"));

        // Unknown not required field (isolated)
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                    + "?aardvark");
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("cannot parse name value pair"));
        }

        // Unknown and required field
        try {
            testObject = new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                    + "?req-aardvark=true");
            fail("Expecting ColchestercoinURIParseException");
        } catch (ColchestercoinURIParseException e) {
            assertTrue(e.getMessage().contains("req-aardvark"));
        }
    }

    @Test
    public void brokenURIs() throws ColchestercoinURIParseException {
        // Check we can parse the incorrectly formatted URIs produced by blockchain.info and its iPhone app.
        String str = "colchestercoin://1KzTSfqjF2iKCduwz59nv2uqh1W2JsTxZH?amount=0.01000000";
        ColchestercoinURI uri = new ColchestercoinURI(str);
        assertEquals("1KzTSfqjF2iKCduwz59nv2uqh1W2JsTxZH", uri.getAddress().toString());
        assertEquals(Utils.toNanoCoins(0, 1), uri.getAmount());
    }

    @Test(expected = ColchestercoinURIParseException.class)
    public void testBad_AmountTooPrecise() throws ColchestercoinURIParseException {
        new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                + "?amount=0.123456789");
    }

    @Test(expected = ColchestercoinURIParseException.class)
    public void testBad_NegativeAmount() throws ColchestercoinURIParseException {
        new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                + "?amount=-1");
    }

    @Test(expected = ColchestercoinURIParseException.class)
    public void testBad_TooLargeAmount() throws ColchestercoinURIParseException {
        new ColchestercoinURI(MainNetParams.get(), ColchestercoinURI.BITCOIN_SCHEME + ":" + MAINNET_GOOD_ADDRESS
                + "?amount=100000000");
    }
}
