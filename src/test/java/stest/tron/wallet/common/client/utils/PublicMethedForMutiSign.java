package stest.tron.wallet.common.client.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.testng.Assert;
import org.tron.api.GrpcAPI;
import org.tron.api.GrpcAPI.AccountNetMessage;
import org.tron.api.GrpcAPI.AccountResourceMessage;
import org.tron.api.GrpcAPI.AssetIssueList;
import org.tron.api.GrpcAPI.BytesMessage;
import org.tron.api.GrpcAPI.DelegatedResourceList;
import org.tron.api.GrpcAPI.DelegatedResourceMessage;
import org.tron.api.GrpcAPI.EmptyMessage;
import org.tron.api.GrpcAPI.ExchangeList;
import org.tron.api.GrpcAPI.Return;
import org.tron.api.GrpcAPI.Return.response_code;
import org.tron.api.GrpcAPI.TransactionExtention;
import org.tron.api.GrpcAPI.TransactionSignWeight;
import org.tron.api.WalletGrpc;
import org.tron.api.WalletSolidityGrpc;
import org.tron.common.crypto.ECKey;
import org.tron.common.crypto.ECKey.ECDSASignature;
import org.tron.common.crypto.Hash;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Utils;
import org.tron.core.Wallet;
import org.tron.core.exception.CancelException;
import org.tron.keystore.WalletFile;
import org.tron.protos.Contract;
import org.tron.protos.Contract.CreateSmartContract;
import org.tron.protos.Contract.CreateSmartContract.Builder;
import org.tron.protos.Contract.UpdateSettingContract;
import org.tron.protos.Protocol.Account;
import org.tron.protos.Protocol.Block;
import org.tron.protos.Protocol.DelegatedResourceAccountIndex;
import org.tron.protos.Protocol.Exchange;
import org.tron.protos.Protocol.Key;
import org.tron.protos.Protocol.Permission;
import org.tron.protos.Protocol.SmartContract;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.Protocol.Transaction.Result;
import org.tron.protos.Protocol.TransactionInfo;
import stest.tron.wallet.common.client.Parameter.CommonConstant;
import stest.tron.wallet.common.client.WalletClient;


public class PublicMethedForMutiSign {

  Wallet wallet = new Wallet();
  //Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
  private static final String FilePath = "Wallet";
  private static List<WalletFile> walletFile = new ArrayList<>();
  private static final Logger logger = LoggerFactory.getLogger("TestLogger");
  //private WalletGrpc.WalletBlockingStub blockingStubFull = null;
  //private WalletSolidityGrpc.WalletSolidityBlockingStub blockingStubSolidity = null;
  /**
   * constructor.
   */

  public static String createAssetIssue(byte[] address, String name, Long totalSupply,
      Integer trxNum, Integer icoNum, Long startTime, Long endTime, Integer voteScore,
      String description, String url, Long freeAssetNetLimit, Long publicFreeAssetNetLimit,
      Long fronzenAmount, Long frozenDay, String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    ECKey ecKey = temKey;
    try {
      Contract.AssetIssueContract.Builder builder = Contract.AssetIssueContract.newBuilder();
      builder.setOwnerAddress(ByteString.copyFrom(address));
      builder.setName(ByteString.copyFrom(name.getBytes()));
      builder.setTotalSupply(totalSupply);
      builder.setTrxNum(trxNum);
      builder.setNum(icoNum);
      builder.setStartTime(startTime);
      builder.setEndTime(endTime);
      builder.setVoteScore(voteScore);
      builder.setDescription(ByteString.copyFrom(description.getBytes()));
      builder.setUrl(ByteString.copyFrom(url.getBytes()));
      builder.setFreeAssetNetLimit(freeAssetNetLimit);
      builder.setPublicFreeAssetNetLimit(publicFreeAssetNetLimit);
      Contract.AssetIssueContract.FrozenSupply.Builder frozenBuilder =
          Contract.AssetIssueContract.FrozenSupply.newBuilder();
      frozenBuilder.setFrozenAmount(fronzenAmount);
      frozenBuilder.setFrozenDays(frozenDay);
      builder.addFrozenSupply(0, frozenBuilder);

      Transaction transaction = blockingStubFull.createAssetIssue(builder.build());
      if (transaction == null || transaction.getRawData().getContractCount() == 0) {
        logger.info("transaction == null");
        return null;
      }
      return ByteArray.toHexString(transaction.toByteArray());
      //transaction = signTransaction(ecKey, transaction);

      //Return response = blockingStubFull.broadcastTransaction(transaction);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static boolean broadcastTransaction(Transaction transaction,WalletGrpc.WalletBlockingStub blockingStubFull) {
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      logger.info(Integer.toString(response.getCode().getNumber()));
      logger.info(Integer.toString(response.getCodeValue()));
      return false;
    } else {
      return true;
    }

  }
  /**
   * constructor.
   */

  public static String createAssetIssue(byte[] address, String name, Long totalSupply,
      Integer trxNum, Integer icoNum, int precision,Long startTime, Long endTime, Integer voteScore,
      String description, String url, Long freeAssetNetLimit, Long publicFreeAssetNetLimit,
      Long fronzenAmount, Long frozenDay, String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    ECKey ecKey = temKey;
    try {
      Contract.AssetIssueContract.Builder builder = Contract.AssetIssueContract.newBuilder();
      builder.setOwnerAddress(ByteString.copyFrom(address));
      builder.setName(ByteString.copyFrom(name.getBytes()));
      builder.setTotalSupply(totalSupply);
      builder.setTrxNum(trxNum);
      builder.setNum(icoNum);
      builder.setStartTime(startTime);
      builder.setEndTime(endTime);
      builder.setVoteScore(voteScore);
      builder.setPrecision(precision);
      builder.setDescription(ByteString.copyFrom(description.getBytes()));
      builder.setUrl(ByteString.copyFrom(url.getBytes()));
      builder.setFreeAssetNetLimit(freeAssetNetLimit);
      builder.setPublicFreeAssetNetLimit(publicFreeAssetNetLimit);
      Contract.AssetIssueContract.FrozenSupply.Builder frozenBuilder =
          Contract.AssetIssueContract.FrozenSupply.newBuilder();
      frozenBuilder.setFrozenAmount(fronzenAmount);
      frozenBuilder.setFrozenDays(frozenDay);
      builder.addFrozenSupply(0, frozenBuilder);

      Transaction transaction = blockingStubFull.createAssetIssue(builder.build());
      if (transaction == null || transaction.getRawData().getContractCount() == 0) {
        logger.info("transaction == null");
        return null;
      }
      //transaction = signTransaction(ecKey, transaction);

      //Return response = blockingStubFull.broadcastTransaction(transaction);
      return ByteArray
          .toHexString(transaction.toByteArray());
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * constructor.
   */

  public static Return createAssetIssue2(byte[] address, String name, Long totalSupply,
      Integer trxNum, Integer icoNum, Long startTime, Long endTime, Integer voteScore,
      String description, String url, Long freeAssetNetLimit, Long publicFreeAssetNetLimit,
      Long fronzenAmount, Long frozenDay, String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    ECKey ecKey = temKey;
    //Protocol.Account search = queryAccount(ecKey, blockingStubFull);
    try {
      Contract.AssetIssueContract.Builder builder = Contract.AssetIssueContract.newBuilder();
      builder.setOwnerAddress(ByteString.copyFrom(address));
      builder.setName(ByteString.copyFrom(name.getBytes()));
      builder.setTotalSupply(totalSupply);
      builder.setTrxNum(trxNum);
      builder.setNum(icoNum);
      builder.setStartTime(startTime);
      builder.setEndTime(endTime);
      builder.setVoteScore(voteScore);
      builder.setDescription(ByteString.copyFrom(description.getBytes()));
      builder.setUrl(ByteString.copyFrom(url.getBytes()));
      builder.setFreeAssetNetLimit(freeAssetNetLimit);
      builder.setPublicFreeAssetNetLimit(publicFreeAssetNetLimit);
      //builder.setPublicFreeAssetNetUsage();
      //builder.setPublicLatestFreeNetTime();
      Contract.AssetIssueContract.FrozenSupply.Builder frozenBuilder =
          Contract.AssetIssueContract.FrozenSupply.newBuilder();
      frozenBuilder.setFrozenAmount(fronzenAmount);
      frozenBuilder.setFrozenDays(frozenDay);
      builder.addFrozenSupply(0, frozenBuilder);

      TransactionExtention transactionExtention = blockingStubFull
          .createAssetIssue2(builder.build());

      if (transactionExtention == null) {
        return transactionExtention.getResult();
      }
      Return ret = transactionExtention.getResult();
      if (!ret.getResult()) {
        System.out.println("Code = " + ret.getCode());
        System.out.println("Message = " + ret.getMessage().toStringUtf8());
        return ret;
      } else {
        System.out.println("Code = " + ret.getCode());
        System.out.println("Message = " + ret.getMessage().toStringUtf8());
      }
      Transaction transaction = transactionExtention.getTransaction();
      if (transaction == null || transaction.getRawData().getContractCount() == 0) {
        System.out.println("Transaction is empty");
        return transactionExtention.getResult();
      }
      System.out.println(
          "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
      transaction = signTransaction(ecKey, transaction);

      Return response = blockingStubFull.broadcastTransaction(transaction);
      if (response.getResult() == false) {
        logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
        return response;
      } else {
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      return ret;
    } catch (Exception ex) {
      ex.printStackTrace();
      //return false;
      return Return.getDefaultInstance();
    }
  }
  /**
   * constructor.
   */

  public static Account queryAccountByAddress(byte[] address,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ByteString addressBs = ByteString.copyFrom(address);
    Account request = Account.newBuilder().setAddress(addressBs).build();
    return blockingStubFull.getAccount(request);
  }
  /**
   * constructor.
   */

  public static Account queryAccount(byte[] address, WalletGrpc
      .WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ByteString addressBs = ByteString.copyFrom(address);
    Account request = Account.newBuilder().setAddress(addressBs).build();
    return blockingStubFull.getAccount(request);
  }
  /**
   * constructor.
   */

  /**
   * constructor.
   */

  public static Account queryAccount(byte[] address, WalletSolidityGrpc
          .WalletSolidityBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ByteString addressBs = ByteString.copyFrom(address);
    Account request = Account.newBuilder().setAddress(addressBs).build();
    return blockingStubFull.getAccount(request);
  }


  /**
   * constructor.
   */

  public static Account queryAccount(String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    byte[] address;
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    ECKey ecKey = temKey;
    if (ecKey == null) {
      String pubKey = loadPubKey(); //04 PubKey[128]
      if (StringUtils.isEmpty(pubKey)) {
        logger.warn("Warning: QueryAccount failed, no wallet address !!");
        return null;
      }
      byte[] pubKeyAsc = pubKey.getBytes();
      byte[] pubKeyHex = Hex.decode(pubKeyAsc);
      ecKey = ECKey.fromPublicOnly(pubKeyHex);
    }
    return grpcQueryAccount(ecKey.getAddress(), blockingStubFull);
  }
  /**
   * constructor.
   */

  public static String loadPubKey() {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    char[] buf = new char[0x100];
    return String.valueOf(buf, 32, 130);
  }
  /**
   * constructor.
   */

  public static byte[] getAddress(ECKey ecKey) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);

    return ecKey.getAddress();
  }
  /**
   * constructor.
   */

  public static Account grpcQueryAccount(byte[] address,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ByteString addressBs = ByteString.copyFrom(address);
    Account request = Account.newBuilder().setAddress(addressBs).build();
    return blockingStubFull.getAccount(request);
  }
  /**
   * constructor.
   */

  public static Block getBlock(long blockNum,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    GrpcAPI.NumberMessage.Builder builder = GrpcAPI.NumberMessage.newBuilder();
    builder.setNum(blockNum);
    return blockingStubFull.getBlockByNum(builder.build());
  }
  /**
   * constructor.
   */

  public static Transaction signTransaction(ECKey ecKey,
      Transaction transaction) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    if (ecKey == null || ecKey.getPrivKey() == null) {
      //logger.warn("Warning: Can't sign,there is no private key !!");
      return null;
    }
    transaction = TransactionUtils.setTimestamp(transaction);
    return TransactionUtils.sign(transaction, ecKey);
  }
  /**
   * constructor.
   */

  public static boolean participateAssetIssue(byte[] to, byte[] assertName, long amount,
      byte[] from, String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    Contract.ParticipateAssetIssueContract.Builder builder = Contract.ParticipateAssetIssueContract
        .newBuilder();
    ByteString bsTo = ByteString.copyFrom(to);
    ByteString bsName = ByteString.copyFrom(assertName);
    ByteString bsOwner = ByteString.copyFrom(from);
    builder.setToAddress(bsTo);
    builder.setAssetName(bsName);
    builder.setOwnerAddress(bsOwner);
    builder.setAmount(amount);
    Contract.ParticipateAssetIssueContract contract = builder.build();
    Transaction transaction = blockingStubFull.participateAssetIssue(contract);
    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      logger.info(Integer.toString(response.getCode().getNumber()));
      logger.info(Integer.toString(response.getCodeValue()));

      return false;
    } else {
      //logger.info(name);
      return true;
    }
  }
  /**
   * constructor.
   */

  public static Return participateAssetIssue2(byte[] to, byte[] assertName, long amount,
      byte[] from, String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    Contract.ParticipateAssetIssueContract.Builder builder = Contract.ParticipateAssetIssueContract
        .newBuilder();
    ByteString bsTo = ByteString.copyFrom(to);
    ByteString bsName = ByteString.copyFrom(assertName);
    ByteString bsOwner = ByteString.copyFrom(from);
    builder.setToAddress(bsTo);
    builder.setAssetName(bsName);
    builder.setOwnerAddress(bsOwner);
    builder.setAmount(amount);
    Contract.ParticipateAssetIssueContract contract = builder.build();

    TransactionExtention transactionExtention = blockingStubFull.participateAssetIssue2(contract);
    if (transactionExtention == null) {
      return transactionExtention.getResult();
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return ret;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return transactionExtention.getResult();
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));

    //Protocol.Transaction transaction = blockingStubFull.participateAssetIssue(contract);

    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      logger.info(Integer.toString(response.getCode().getNumber()));
      logger.info(Integer.toString(response.getCodeValue()));

      return response;
    }
    return ret;
  }
  /**
   * constructor.
   */

  public static String freezeBalance(byte[] addRess, long freezeBalance, long freezeDuration,
      String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    byte[] address = addRess;
    long frozenBalance = freezeBalance;
    long frozenDuration = freezeDuration;
    //String priKey = testKey002;
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;
    Block currentBlock = blockingStubFull.getNowBlock(EmptyMessage.newBuilder().build());
    final Long beforeBlockNum = currentBlock.getBlockHeader().getRawData().getNumber();
    Account beforeFronzen = queryAccount(priKey, blockingStubFull);
    Long beforeFrozenBalance = 0L;
    //Long beforeBandwidth     = beforeFronzen.getBandwidth();
    if (beforeFronzen.getFrozenCount() != 0) {
      beforeFrozenBalance = beforeFronzen.getFrozen(0).getFrozenBalance();
      //beforeBandwidth     = beforeFronzen.getBandwidth();
      //logger.info(Long.toString(beforeFronzen.getBandwidth()));
      logger.info(Long.toString(beforeFronzen.getFrozen(0).getFrozenBalance()));
    }

    Contract.FreezeBalanceContract.Builder builder = Contract.FreezeBalanceContract.newBuilder();
    ByteString byteAddreess = ByteString.copyFrom(address);

    builder.setOwnerAddress(byteAddreess).setFrozenBalance(frozenBalance)
        .setFrozenDuration(frozenDuration);

    Contract.FreezeBalanceContract contract = builder.build();
    Transaction transaction = blockingStubFull.freezeBalance(contract);

    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      logger.info("transaction = null");
      return null;
    }

    transaction = TransactionUtils.setTimestamp(transaction);
    return ByteArray.toHexString(transaction.toByteArray());

  }
  /**
   * constructor.
   */

  public static Return freezeBalance2(byte[] addRess, long freezeBalance, long freezeDuration,
      String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    byte[] address = addRess;
    long frozenBalance = freezeBalance;
    long frozenDuration = freezeDuration;
    //String priKey = testKey002;
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;
    Block currentBlock = blockingStubFull.getNowBlock(EmptyMessage.newBuilder().build());
    final Long beforeBlockNum = currentBlock.getBlockHeader().getRawData().getNumber();
    Account beforeFronzen = queryAccount(priKey, blockingStubFull);
    Long beforeFrozenBalance = 0L;
    //Long beforeBandwidth     = beforeFronzen.getBandwidth();
    if (beforeFronzen.getFrozenCount() != 0) {
      beforeFrozenBalance = beforeFronzen.getFrozen(0).getFrozenBalance();
      //beforeBandwidth     = beforeFronzen.getBandwidth();
      //logger.info(Long.toString(beforeFronzen.getBandwidth()));
      logger.info(Long.toString(beforeFronzen.getFrozen(0).getFrozenBalance()));
    }

    Contract.FreezeBalanceContract.Builder builder = Contract.FreezeBalanceContract.newBuilder();
    ByteString byteAddreess = ByteString.copyFrom(address);

    builder.setOwnerAddress(byteAddreess).setFrozenBalance(frozenBalance)
        .setFrozenDuration(frozenDuration);

    Contract.FreezeBalanceContract contract = builder.build();

    TransactionExtention transactionExtention = blockingStubFull.freezeBalance2(contract);
    if (transactionExtention == null) {
      return transactionExtention.getResult();
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return ret;
    } else {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return transactionExtention.getResult();
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));

    transaction = TransactionUtils.setTimestamp(transaction);
    transaction = TransactionUtils.sign(transaction, ecKey);
    Return response = blockingStubFull.broadcastTransaction(transaction);

    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return response;
    }

    Long afterBlockNum = 0L;

    while (afterBlockNum < beforeBlockNum) {
      Block currentBlock1 = blockingStubFull.getNowBlock(EmptyMessage.newBuilder().build());
      afterBlockNum = currentBlock1.getBlockHeader().getRawData().getNumber();
    }

    Account afterFronzen = queryAccount(priKey, blockingStubFull);
    Long afterFrozenBalance = afterFronzen.getFrozen(0).getFrozenBalance();
    logger.info(Long.toString(afterFronzen.getFrozen(0).getFrozenBalance()));
    logger.info("beforefronen" + beforeFrozenBalance.toString() + "    afterfronzen"
        + afterFrozenBalance.toString());
    Assert.assertTrue(afterFrozenBalance - beforeFrozenBalance == freezeBalance);
    return ret;
  }

  /**
   * constructor.
   */

  public static Boolean unFreezeBalance(byte[] address, String priKey, int resourceCode,
      byte[] receiverAddress, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;
    Contract.UnfreezeBalanceContract.Builder builder = Contract.UnfreezeBalanceContract
        .newBuilder();
    ByteString byteAddreess = ByteString.copyFrom(address);
    builder.setOwnerAddress(byteAddreess).setResourceValue(resourceCode);
    if (receiverAddress != null) {
      ByteString receiverAddressBytes = ByteString.copyFrom(receiverAddress);
      builder.setReceiverAddress(receiverAddressBytes);
    }

    Contract.UnfreezeBalanceContract contract = builder.build();
    Transaction transaction =  blockingStubFull.unfreezeBalance(contract);
    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    } else {
      return true;
    }
  }
  /**
   * constructor.
   */

  public static String sendcoin(byte[] to, long amount, byte[] owner, String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    //String priKey = testKey002;
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;
    //Protocol.Account search = queryAccount(priKey, blockingStubFull);

    Contract.TransferContract.Builder builder = Contract.TransferContract.newBuilder();
    ByteString bsTo = ByteString.copyFrom(to);
    ByteString bsOwner = ByteString.copyFrom(owner);
    builder.setToAddress(bsTo);
    builder.setOwnerAddress(bsOwner);
    builder.setAmount(amount);

    Contract.TransferContract contract = builder.build();
    Transaction transaction = blockingStubFull.createTransaction(contract);
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      logger.info("transaction ==null");
      return null;
    }
    //Test raw data
    /*    Protocol.Transaction.raw.Builder builder1 = transaction.getRawData().toBuilder();
    builder1.setData(ByteString.copyFromUtf8("12345678"));
    Transaction.Builder builder2 = transaction.toBuilder();
    builder2.setRawData(builder1);
    transaction = builder2.build();*/
    return ByteArray.toHexString(transaction.toByteArray());

  }

  /**
   * constructor.
   */

  public static Return sendcoin2(byte[] to, long amount, byte[] owner, String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;
    //Protocol.Account search = queryAccount(priKey, blockingStubFull);

    Contract.TransferContract.Builder builder = Contract.TransferContract.newBuilder();
    ByteString bsTo = ByteString.copyFrom(to);
    ByteString bsOwner = ByteString.copyFrom(owner);
    builder.setToAddress(bsTo);
    builder.setOwnerAddress(bsOwner);
    builder.setAmount(amount);

    Contract.TransferContract contract = builder.build();
    TransactionExtention transactionExtention = blockingStubFull.createTransaction2(contract);
    if (transactionExtention == null) {
      return transactionExtention.getResult();
    }

    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return ret;
    } else {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
    }

    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return transactionExtention.getResult();
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));

    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return response;
    }
    return ret;
  }
  /**
   * constructor.
   */

  public static String sendcoinGetTransactionId(byte[] to, long amount, byte[] owner, String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    //String priKey = testKey002;
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;
    //Protocol.Account search = queryAccount(priKey, blockingStubFull);

    Contract.TransferContract.Builder builder = Contract.TransferContract.newBuilder();
    ByteString bsTo = ByteString.copyFrom(to);
    ByteString bsOwner = ByteString.copyFrom(owner);
    builder.setToAddress(bsTo);
    builder.setOwnerAddress(bsOwner);
    builder.setAmount(amount);

    Contract.TransferContract contract = builder.build();
    Transaction transaction = blockingStubFull.createTransaction(contract);
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      logger.info("transaction ==null");
      return null;
    }
    //Test raw data
    /*    Protocol.Transaction.raw.Builder builder1 = transaction.getRawData().toBuilder();
    builder1.setData(ByteString.copyFromUtf8("12345678"));
    Transaction.Builder builder2 = transaction.toBuilder();
    builder2.setRawData(builder1);
    transaction = builder2.build();*/

    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return null;
    } else {
      return ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray()));
    }
  }
  /**
   * constructor.
   */

  public static Optional<Transaction> getTransactionById(String txId,
      WalletSolidityGrpc.WalletSolidityBlockingStub blockingStubFull) {
    ByteString bsTxid = ByteString.copyFrom(ByteArray.fromHexString(txId));
    BytesMessage request = BytesMessage.newBuilder().setValue(bsTxid).build();
    Transaction transaction = blockingStubFull.getTransactionById(request);
    return Optional.ofNullable(transaction);
  }
  /**
   * constructor.
   */

  public static Optional<Transaction> getTransactionById(String txId,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    ByteString bsTxid = ByteString.copyFrom(ByteArray.fromHexString(txId));
    BytesMessage request = BytesMessage.newBuilder().setValue(bsTxid).build();
    Transaction transaction = blockingStubFull.getTransactionById(request);
    return Optional.ofNullable(transaction);
  }
  /**
   * constructor.
   */

  public static Optional<Transaction> getTransactionByIdSolidity(String txId,
      WalletGrpc.WalletBlockingStub blockingStubSolidity) {
    ByteString bsTxid = ByteString.copyFrom(ByteArray.fromHexString(txId));
    BytesMessage request = BytesMessage.newBuilder().setValue(bsTxid).build();
    Transaction transaction = blockingStubSolidity.getTransactionById(request);
    return Optional.ofNullable(transaction);
  }
  /**
   * constructor.
   */

  public static String printTransaction(Transaction transaction) {
    String result = "";
    result += "hash: ";
    result += "\n";
    result += ByteArray.toHexString(Sha256Hash.hash(transaction.toByteArray()));
    result += "\n";
    result += "txid: ";
    result += "\n";
    result += ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray()));
    result += "\n";

    if (transaction.getRawData() != null) {
      result += "raw_data: ";
      result += "\n";
      result += "{";
      result += "\n";
      result += printTransactionRow(transaction.getRawData());
      result += "}";
      result += "\n";
    }

    return result;
  }
  /**
   * constructor.
   */

  public static long printTransactionRow(Transaction.raw raw) {
    long timestamp = raw.getTimestamp();

    return timestamp;
  }
  /**
   * constructor.
   */

  public static boolean updateAsset(byte[] address, byte[] description, byte[] url, long newLimit,
      long newPublicLimit, String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;
    Contract.UpdateAssetContract.Builder builder =
        Contract.UpdateAssetContract.newBuilder();
    ByteString basAddreess = ByteString.copyFrom(address);
    builder.setDescription(ByteString.copyFrom(description));
    builder.setUrl(ByteString.copyFrom(url));
    builder.setNewLimit(newLimit);
    builder.setNewPublicLimit(newPublicLimit);
    builder.setOwnerAddress(basAddreess);

    Contract.UpdateAssetContract contract
        = builder.build();
    Transaction transaction = blockingStubFull.updateAsset(contract);

    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      return false;
    }

    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    } else {
      return true;
    }
  }
  /**
   * constructor.
   */

  public static Return updateAsset2(byte[] address, byte[] description, byte[] url, long newLimit,
      long newPublicLimit, String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;
    Contract.UpdateAssetContract.Builder builder =
        Contract.UpdateAssetContract.newBuilder();
    ByteString basAddreess = ByteString.copyFrom(address);
    builder.setDescription(ByteString.copyFrom(description));
    builder.setUrl(ByteString.copyFrom(url));
    builder.setNewLimit(newLimit);
    builder.setNewPublicLimit(newPublicLimit);
    builder.setOwnerAddress(basAddreess);

    Contract.UpdateAssetContract contract
        = builder.build();

    TransactionExtention transactionExtention = blockingStubFull.updateAsset2(contract);
    if (transactionExtention == null) {
      return transactionExtention.getResult();
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return ret;
    } else {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return transactionExtention.getResult();
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));

    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return response;
    }
    return ret;
  }
  /**
   * constructor.
   */

  public static boolean transferAsset(byte[] to, byte[] assertName, long amount, byte[] address,
      String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    Contract.TransferAssetContract.Builder builder = Contract.TransferAssetContract.newBuilder();
    ByteString bsTo = ByteString.copyFrom(to);
    ByteString bsName = ByteString.copyFrom(assertName);
    ByteString bsOwner = ByteString.copyFrom(address);
    builder.setToAddress(bsTo);
    builder.setAssetName(bsName);
    builder.setOwnerAddress(bsOwner);
    builder.setAmount(amount);

    Contract.TransferAssetContract contract = builder.build();
    Transaction transaction = blockingStubFull.transferAsset(contract);

    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      if (transaction == null) {
        logger.info("transaction == null");
      } else {
        logger.info("transaction.getRawData().getContractCount() == 0");
      }
      return false;
    }
    transaction = signTransaction(ecKey, transaction);

    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    } else {
      return true;
    }
  }
  /**
   * constructor.
   */

  public static boolean updateAccount(byte[] addressBytes, byte[] accountNameBytes, String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    Contract.AccountUpdateContract.Builder builder = Contract.AccountUpdateContract.newBuilder();
    ByteString basAddreess = ByteString.copyFrom(addressBytes);
    ByteString bsAccountName = ByteString.copyFrom(accountNameBytes);

    builder.setAccountName(bsAccountName);
    builder.setOwnerAddress(basAddreess);

    Contract.AccountUpdateContract contract = builder.build();
    Transaction transaction = blockingStubFull.updateAccount(contract);

    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      logger.info("Please check!!! transaction == null");
      return false;
    }
    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info("Please check!!! response.getresult==false");
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    } else {
      return true;
    }
  }
  /**
   * constructor.
   */

  public static boolean waitSolidityNodeSynFullNodeData(WalletGrpc.WalletBlockingStub
      blockingStubFull, WalletSolidityGrpc.WalletSolidityBlockingStub blockingStubSolidity) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    Block currentBlock = blockingStubFull.getNowBlock(EmptyMessage.newBuilder().build());
    Block solidityCurrentBlock = blockingStubSolidity.getNowBlock(EmptyMessage
        .newBuilder().build());
    Integer wait = 0;
    logger.info("Fullnode block num is " + Long.toString(currentBlock
        .getBlockHeader().getRawData().getNumber()));
    while (solidityCurrentBlock.getBlockHeader().getRawData().getNumber()
        < currentBlock.getBlockHeader().getRawData().getNumber() + 1 && wait <= 10) {
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      logger.info("Soliditynode num is " + Long.toString(solidityCurrentBlock
          .getBlockHeader().getRawData().getNumber()));
      solidityCurrentBlock = blockingStubSolidity.getNowBlock(EmptyMessage.newBuilder()
          .build());
      if (wait == 10) {
        logger.info("Didn't syn,skip to next case.");
        return false;
      }
      wait++;
    }
    return true;
  }
  /**
   * constructor.
   */

  public static boolean waitProduceNextBlock(WalletGrpc.WalletBlockingStub
      blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    Block currentBlock = blockingStubFull.getNowBlock(EmptyMessage.newBuilder().build());
    final Long currentNum = currentBlock.getBlockHeader().getRawData().getNumber();

    Block nextBlock = blockingStubFull.getNowBlock(EmptyMessage.newBuilder().build());
    Long nextNum = nextBlock.getBlockHeader().getRawData().getNumber();

    Integer wait = 0;
    logger.info("Block num is " + Long.toString(currentBlock
        .getBlockHeader().getRawData().getNumber()));
    while (nextNum <= currentNum + 1 && wait <= 15) {
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      logger.info("Wait to produce next block");
      nextBlock = blockingStubFull.getNowBlock(EmptyMessage.newBuilder().build());
      nextNum = nextBlock.getBlockHeader().getRawData().getNumber();
      if (wait == 15) {
        logger.info("These 45 second didn't produce a block,please check.");
        return false;
      }
      wait++;
    }
    logger.info("quit normally");
    return true;
  }
  /**
   * constructor.
   */

  public static AccountNetMessage getAccountNet(byte[] address, WalletGrpc.WalletBlockingStub
      blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ByteString addressBs = ByteString.copyFrom(address);
    Account request = Account.newBuilder().setAddress(addressBs).build();
    return blockingStubFull.getAccountNet(request);
  }

  /*  public static byte[] addPreFix(byte[] address) {
  Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
  Config config = Configuration.getByPath("testng.conf");
  byte ADD_PRE_FIX_BYTE_MAINNET = (byte) 0x41;   //41 + address
  byte ADD_PRE_FIX_BYTE_TESTNET = (byte) 0xa0;   //a0 + address
  byte[] preFix = new byte[1];
  if (config.hasPath("net.type") && "mainnet".equalsIgnoreCase(config.getString("net.type"))) {
    WalletClient.setAddressPreFixByte(ADD_PRE_FIX_BYTE_MAINNET);
    preFix[0] = ADD_PRE_FIX_BYTE_MAINNET;
   }else {
      WalletClient.setAddressPreFixByte(ADD_PRE_FIX_BYTE_TESTNET);
      preFix[0] = ADD_PRE_FIX_BYTE_TESTNET;
    }
    byte[] finalAddress = new byte[preFix.length+address.length];
    System.arraycopy(preFix, 0, finalAddress, 0, preFix.length);
    System.arraycopy(address, 0, finalAddress, preFix.length, address.length);
    return finalAddress;

  }*/
  /**
   * constructor.
   */

  public static byte[] getFinalAddress(String priKey) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    WalletClient walletClient;
    walletClient = new WalletClient(priKey);
    //walletClient.init(0);
    return walletClient.getAddress();
  }
  /**
   * constructor.
   */

  public static boolean createAccount(byte[] ownerAddress, byte[] newAddress, String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    byte[] owner = ownerAddress;
    Contract.AccountCreateContract.Builder builder = Contract.AccountCreateContract.newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(owner));
    builder.setAccountAddress(ByteString.copyFrom(newAddress));
    Contract.AccountCreateContract contract = builder.build();
    Transaction transaction = blockingStubFull.createAccount(contract);
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      logger.info("transaction == null");
    }
    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    } else {
      return true;
    }

  }
  /**
   * constructor.
   */

  public static Return createAccount2(byte[] ownerAddress, byte[] newAddress, String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    byte[] owner = ownerAddress;
    Contract.AccountCreateContract.Builder builder = Contract.AccountCreateContract.newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(owner));
    builder.setAccountAddress(ByteString.copyFrom(newAddress));
    Contract.AccountCreateContract contract = builder.build();

    TransactionExtention transactionExtention = blockingStubFull.createAccount2(contract);

    if (transactionExtention == null) {
      return transactionExtention.getResult();
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return ret;
    } else {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return transactionExtention.getResult();
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));

    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return response;
    }
    return ret;
  }
  /**
   * constructor.
   */

  public static boolean createProposal(byte[] ownerAddress, String priKey,
      HashMap<Long, Long> parametersMap, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    byte[] owner = ownerAddress;
    Contract.ProposalCreateContract.Builder builder = Contract.ProposalCreateContract.newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(owner));
    builder.putAllParameters(parametersMap);

    Contract.ProposalCreateContract contract = builder.build();
    TransactionExtention transactionExtention = blockingStubFull.proposalCreate(contract);
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    } else {
      return true;
    }
  }
  /**
   * constructor.
   */

  public static boolean approveProposal(byte[] ownerAddress, String priKey, long id,
      boolean isAddApproval, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    byte[] owner = ownerAddress;
    Contract.ProposalApproveContract.Builder builder = Contract.ProposalApproveContract
        .newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(owner));
    builder.setProposalId(id);
    builder.setIsAddApproval(isAddApproval);
    Contract.ProposalApproveContract contract = builder.build();
    TransactionExtention transactionExtention = blockingStubFull.proposalApprove(contract);
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    } else {
      return true;
    }
  }
  /**
   * constructor.
   */

  public static boolean deleteProposal(byte[] ownerAddress, String priKey, long id,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    byte[] owner = ownerAddress;
    Contract.ProposalDeleteContract.Builder builder = Contract.ProposalDeleteContract.newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(owner));
    builder.setProposalId(id);

    Contract.ProposalDeleteContract contract = builder.build();
    TransactionExtention transactionExtention = blockingStubFull.proposalDelete(contract);
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    } else {
      return true;
    }
  }
  /**
   * constructor.
   */

  public static boolean printAddress(String key) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    logger.info(key);
    logger.info(ByteArray.toHexString(getFinalAddress(key)));
    logger.info(Base58.encode58Check(getFinalAddress(key)));
    return true;
  }
  /**
   * constructor.
   */

  public static ArrayList<String>  getAddressInfo(String key) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ArrayList<String> accountList = new ArrayList<String>();
    accountList.add(key);
    accountList.add(ByteArray.toHexString(getFinalAddress(key)));
    accountList.add(Base58.encode58Check(getFinalAddress(key)));
    return accountList;
  }

  /**
   * constructor.
   */

  public static boolean setAccountId(byte[] accountIdBytes, byte[] ownerAddress, String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    byte[] owner = ownerAddress;
    Contract.SetAccountIdContract.Builder builder = Contract.SetAccountIdContract.newBuilder();
    ByteString bsAddress = ByteString.copyFrom(owner);
    ByteString bsAccountId = ByteString.copyFrom(accountIdBytes);
    builder.setAccountId(bsAccountId);
    builder.setOwnerAddress(bsAddress);
    Contract.SetAccountIdContract contract = builder.build();
    Transaction transaction = blockingStubFull.setAccountId(contract);
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      logger.info("transaction == null");
    }
    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    } else {
      return true;
    }
  }

  /**
   * constructor.
   */

  public static Boolean freezeBalanceGetEnergy(byte[] addRess, long freezeBalance,
      long freezeDuration,
      int resourceCode, String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    byte[] address = addRess;
    long frozenBalance = freezeBalance;
    long frozenDuration = freezeDuration;
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    Contract.FreezeBalanceContract.Builder builder = Contract.FreezeBalanceContract.newBuilder();
    ByteString byteAddreess = ByteString.copyFrom(address);

    builder.setOwnerAddress(byteAddreess).setFrozenBalance(frozenBalance)
        .setFrozenDuration(frozenDuration).setResourceValue(resourceCode);

    Contract.FreezeBalanceContract contract = builder.build();
    Transaction transaction = blockingStubFull.freezeBalance(contract);

    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      logger.info("transaction = null");
      return false;
    }
    transaction = TransactionUtils.setTimestamp(transaction);
    transaction = TransactionUtils.sign(transaction, ecKey);
    Return response = blockingStubFull.broadcastTransaction(transaction);

    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    }
    return true;
  }
  /**
   * constructor.
   */

  public static AccountResourceMessage getAccountResource(byte[] address,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ByteString addressBs = ByteString.copyFrom(address);
    Account request = Account.newBuilder().setAddress(addressBs).build();
    return blockingStubFull.getAccountResource(request);
  }
  /**
   * constructor.
   */

  public static boolean buyStorage(long quantity, byte[] address,
      String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    Contract.BuyStorageContract.Builder builder = Contract.BuyStorageContract.newBuilder();
    ByteString byteAddress = ByteString.copyFrom(address);
    builder.setOwnerAddress(byteAddress).setQuant(quantity);
    Contract.BuyStorageContract contract = builder.build();
    TransactionExtention transactionExtention = blockingStubFull.buyStorage(contract);
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    } else {
      return true;
    }
  }
  /**
   * constructor.
   */

  public static boolean sellStorage(long quantity, byte[] address,
      String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    Contract.SellStorageContract.Builder builder = Contract.SellStorageContract.newBuilder();
    ByteString byteAddress = ByteString.copyFrom(address);
    builder.setOwnerAddress(byteAddress).setStorageBytes(quantity);
    Contract.SellStorageContract contract = builder.build();
    TransactionExtention transactionExtention = blockingStubFull.sellStorage(contract);
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    } else {
      return true;
    }
  }
  /**
   * constructor.
   */

  public static byte[] deployContract(String contractName, String abiString, String code,
      String data, Long feeLimit, long value,
      long consumeUserResourcePercent, String libraryAddress, String priKey, byte[] ownerAddress,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    return deployContract(contractName, abiString, code, data, feeLimit, value,
        consumeUserResourcePercent, 1000L, "0", 0L, libraryAddress,
        priKey, ownerAddress, blockingStubFull);
  }
  /**
   * constructor.
   */

  public static byte[] deployContract(String contractName, String abiString, String code,
      String data, Long feeLimit, long value,
      long consumeUserResourcePercent, long originEnergyLimit, String tokenId, long tokenValue,
      String libraryAddress, String priKey, byte[] ownerAddress,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    byte[] owner = ownerAddress;
    SmartContract.ABI abi = jsonStr2Abi(abiString);
    if (abi == null) {
      logger.error("abi is null");
      return null;
    }
    //byte[] codeBytes = Hex.decode(code);
    SmartContract.Builder builder = SmartContract.newBuilder();
    builder.setName(contractName);
    builder.setOriginAddress(ByteString.copyFrom(owner));
    builder.setAbi(abi);
    builder.setConsumeUserResourcePercent(consumeUserResourcePercent);
    builder.setOriginEnergyLimit(originEnergyLimit);

    if (value != 0) {

      builder.setCallValue(value);
    }

    byte[] byteCode;
    if (null != libraryAddress) {
      byteCode = replaceLibraryAddress(code, libraryAddress);
    } else {
      byteCode = Hex.decode(code);
    }
    builder.setBytecode(ByteString.copyFrom(byteCode));

    Builder contractBuilder = CreateSmartContract.newBuilder();
    contractBuilder.setOwnerAddress(ByteString.copyFrom(owner));
    contractBuilder.setCallTokenValue(tokenValue);
    contractBuilder.setTokenId(Long.parseLong(tokenId));
    CreateSmartContract contractDeployContract = contractBuilder
        .setNewContract(builder.build()).build();

    TransactionExtention transactionExtention = blockingStubFull
        .deployContract(contractDeployContract);
    if (transactionExtention == null || !transactionExtention.getResult().getResult()) {
      System.out.println("RPC create trx failed!");
      if (transactionExtention != null) {
        System.out.println("Code = " + transactionExtention.getResult().getCode());
        System.out
            .println("Message = " + transactionExtention.getResult().getMessage().toStringUtf8());
      }
      return null;
    }

    final TransactionExtention.Builder texBuilder = TransactionExtention.newBuilder();
    Transaction.Builder transBuilder = Transaction.newBuilder();
    Transaction.raw.Builder rawBuilder = transactionExtention.getTransaction().getRawData()
        .toBuilder();
    rawBuilder.setFeeLimit(feeLimit);
    transBuilder.setRawData(rawBuilder);
    for (int i = 0; i < transactionExtention.getTransaction().getSignatureCount(); i++) {
      ByteString s = transactionExtention.getTransaction().getSignature(i);
      transBuilder.setSignature(i, s);
    }
    for (int i = 0; i < transactionExtention.getTransaction().getRetCount(); i++) {
      Result r = transactionExtention.getTransaction().getRet(i);
      transBuilder.setRet(i, r);
    }
    texBuilder.setTransaction(transBuilder);
    texBuilder.setResult(transactionExtention.getResult());
    texBuilder.setTxid(transactionExtention.getTxid());
    transactionExtention = texBuilder.build();

    byte[] contractAddress = generateContractAddress(transactionExtention.getTransaction(), owner);
    System.out.println(
        "Your smart contract address will be: " + WalletClient.encode58Check(contractAddress));
    if (transactionExtention == null) {
      return null;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return null;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return null;
    }
    transaction = signTransaction(ecKey, transaction);
    System.out.println(
        "txid = " + ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray())));
    contractAddress = generateContractAddress(transaction, owner);
    System.out.println(
        "Your smart contract address will be: " + WalletClient.encode58Check(contractAddress));

    int i = 10;
    Return response = blockingStubFull.broadcastTransaction(transaction);
    while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
        && i > 0) {
      i--;
      response = blockingStubFull.broadcastTransaction(transaction);
      logger.info("repeate times = " + (11 - i));
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (response.getResult() == false) {
      logger.info("Code = " + response.getCode());
      logger.info("Message = " + response.getMessage().toStringUtf8());
      return null;
    } else {
      //logger.info("brodacast succesfully");
      return contractAddress;
    }
  }
  /**
   * constructor.
   */

  public static String deployContractAndGetTransactionInfoById(String contractName,
      String abiString, String code, String data, Long feeLimit, long value,
      long consumeUserResourcePercent, String libraryAddress, String priKey, byte[] ownerAddress,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    return deployContractAndGetTransactionInfoById(contractName, abiString, code, data, feeLimit,
        value, consumeUserResourcePercent, 1000L, "0", 0L, libraryAddress,
        priKey, ownerAddress, blockingStubFull);
  }
  /**
   * constructor.
   */

  public static String deployContractAndGetTransactionInfoById(String contractName,
      String abiString, String code, String data, Long feeLimit, long value,
      long consumeUserResourcePercent, long originEnergyLimit, String tokenId, long tokenValue,
      String libraryAddress, String priKey, byte[] ownerAddress,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    byte[] owner = ownerAddress;
    SmartContract.ABI abi = jsonStr2Abi(abiString);
    if (abi == null) {
      logger.error("abi is null");
      return null;
    }
    //byte[] codeBytes = Hex.decode(code);
    SmartContract.Builder builder = SmartContract.newBuilder();
    builder.setName(contractName);
    builder.setOriginAddress(ByteString.copyFrom(owner));
    builder.setAbi(abi);
    builder.setConsumeUserResourcePercent(consumeUserResourcePercent);
    builder.setOriginEnergyLimit(originEnergyLimit);

    if (value != 0) {

      builder.setCallValue(value);
    }

    byte[] byteCode;
    if (null != libraryAddress) {
      byteCode = replaceLibraryAddress(code, libraryAddress);
    } else {
      byteCode = Hex.decode(code);
    }
    builder.setBytecode(ByteString.copyFrom(byteCode));

    Builder contractBuilder = CreateSmartContract.newBuilder();
    contractBuilder.setOwnerAddress(ByteString.copyFrom(owner));
    contractBuilder.setCallTokenValue(tokenValue);
    contractBuilder.setTokenId(Long.parseLong(tokenId));
    CreateSmartContract contractDeployContract = contractBuilder
        .setNewContract(builder.build()).build();

    TransactionExtention transactionExtention = blockingStubFull
        .deployContract(contractDeployContract);
    if (transactionExtention == null || !transactionExtention.getResult().getResult()) {
      System.out.println("RPC create trx failed!");
      if (transactionExtention != null) {
        System.out.println("Code = " + transactionExtention.getResult().getCode());
        System.out
            .println("Message = " + transactionExtention.getResult().getMessage().toStringUtf8());
      }
      return null;
    }

    final TransactionExtention.Builder texBuilder = TransactionExtention.newBuilder();
    Transaction.Builder transBuilder = Transaction.newBuilder();
    Transaction.raw.Builder rawBuilder = transactionExtention.getTransaction().getRawData()
        .toBuilder();
    rawBuilder.setFeeLimit(feeLimit);
    transBuilder.setRawData(rawBuilder);
    for (int i = 0; i < transactionExtention.getTransaction().getSignatureCount(); i++) {
      ByteString s = transactionExtention.getTransaction().getSignature(i);
      transBuilder.setSignature(i, s);
    }
    for (int i = 0; i < transactionExtention.getTransaction().getRetCount(); i++) {
      Result r = transactionExtention.getTransaction().getRet(i);
      transBuilder.setRet(i, r);
    }
    texBuilder.setTransaction(transBuilder);
    texBuilder.setResult(transactionExtention.getResult());
    texBuilder.setTxid(transactionExtention.getTxid());
    transactionExtention = texBuilder.build();

    if (transactionExtention == null) {
      return null;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return null;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return null;
    }
    transaction = signTransaction(ecKey, transaction);
    System.out.println(
        "txid = " + ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray())));
    byte[] contractAddress = generateContractAddress(transaction, owner);
    System.out.println(
        "Your smart contract address will be: " + WalletClient.encode58Check(contractAddress));
    int i = 10;
    Return response = blockingStubFull.broadcastTransaction(transaction);
    while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
        && i > 0) {
      i--;
      response = blockingStubFull.broadcastTransaction(transaction);
      logger.info("repeate times = " + (11 - i));
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (response.getResult() == false) {
      logger.info("Code = " + response.getCode());
      logger.info("Message = " + response.getMessage().toStringUtf8());
      return null;
    } else {
      //logger.info("brodacast succesfully");
      return ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray()));
    }
  }

  /**
   * constructor.
   */

  public static SmartContract.ABI jsonStr2Abi(String jsonStr) {
    if (jsonStr == null) {
      return null;
    }

    JsonParser jsonParser = new JsonParser();
    JsonElement jsonElementRoot = jsonParser.parse(jsonStr);
    JsonArray jsonRoot = jsonElementRoot.getAsJsonArray();
    SmartContract.ABI.Builder abiBuilder = SmartContract.ABI.newBuilder();
    for (int index = 0; index < jsonRoot.size(); index++) {
      JsonElement abiItem = jsonRoot.get(index);
      boolean anonymous = abiItem.getAsJsonObject().get("anonymous") != null
          ? abiItem.getAsJsonObject().get("anonymous").getAsBoolean() : false;
      final boolean constant = abiItem.getAsJsonObject().get("constant") != null
          ? abiItem.getAsJsonObject().get("constant").getAsBoolean() : false;
      final String name = abiItem.getAsJsonObject().get("name") != null
          ? abiItem.getAsJsonObject().get("name").getAsString() : null;
      JsonArray inputs = abiItem.getAsJsonObject().get("inputs") != null
          ? abiItem.getAsJsonObject().get("inputs").getAsJsonArray() : null;
      final JsonArray outputs = abiItem.getAsJsonObject().get("outputs") != null
          ? abiItem.getAsJsonObject().get("outputs").getAsJsonArray() : null;
      String type = abiItem.getAsJsonObject().get("type") != null
          ? abiItem.getAsJsonObject().get("type").getAsString() : null;
      final boolean payable = abiItem.getAsJsonObject().get("payable") != null
          ? abiItem.getAsJsonObject().get("payable").getAsBoolean() : false;
      final String stateMutability = abiItem.getAsJsonObject().get("stateMutability") != null
          ? abiItem.getAsJsonObject().get("stateMutability").getAsString() : null;
      if (type == null) {
        logger.error("No type!");
        return null;
      }
      if (!type.equalsIgnoreCase("fallback") && null == inputs) {
        logger.error("No inputs!");
        return null;
      }

      SmartContract.ABI.Entry.Builder entryBuilder = SmartContract.ABI.Entry.newBuilder();
      entryBuilder.setAnonymous(anonymous);
      entryBuilder.setConstant(constant);
      if (name != null) {
        entryBuilder.setName(name);
      }

      /* { inputs : optional } since fallback function not requires inputs*/
      if (inputs != null) {
        for (int j = 0; j < inputs.size(); j++) {
          JsonElement inputItem = inputs.get(j);
          if (inputItem.getAsJsonObject().get("name") == null
              || inputItem.getAsJsonObject().get("type") == null) {
            logger.error("Input argument invalid due to no name or no type!");
            return null;
          }
          String inputName = inputItem.getAsJsonObject().get("name").getAsString();
          String inputType = inputItem.getAsJsonObject().get("type").getAsString();
          SmartContract.ABI.Entry.Param.Builder paramBuilder = SmartContract.ABI.Entry.Param
              .newBuilder();
          paramBuilder.setIndexed(false);
          paramBuilder.setName(inputName);
          paramBuilder.setType(inputType);
          entryBuilder.addInputs(paramBuilder.build());
        }
      }

      /* { outputs : optional } */
      if (outputs != null) {
        for (int k = 0; k < outputs.size(); k++) {
          JsonElement outputItem = outputs.get(k);
          if (outputItem.getAsJsonObject().get("name") == null
              || outputItem.getAsJsonObject().get("type") == null) {
            logger.error("Output argument invalid due to no name or no type!");
            return null;
          }
          String outputName = outputItem.getAsJsonObject().get("name").getAsString();
          String outputType = outputItem.getAsJsonObject().get("type").getAsString();
          SmartContract.ABI.Entry.Param.Builder paramBuilder = SmartContract.ABI.Entry.Param
              .newBuilder();
          paramBuilder.setIndexed(false);
          paramBuilder.setName(outputName);
          paramBuilder.setType(outputType);
          entryBuilder.addOutputs(paramBuilder.build());
        }
      }

      entryBuilder.setType(getEntryType(type));
      entryBuilder.setPayable(payable);
      if (stateMutability != null) {
        entryBuilder.setStateMutability(getStateMutability(stateMutability));
      }

      abiBuilder.addEntrys(entryBuilder.build());
    }

    return abiBuilder.build();
  }
  /**
   * constructor.
   */

  public static SmartContract.ABI.Entry.EntryType getEntryType(String type) {
    switch (type) {
      case "constructor":
        return SmartContract.ABI.Entry.EntryType.Constructor;
      case "function":
        return SmartContract.ABI.Entry.EntryType.Function;
      case "event":
        return SmartContract.ABI.Entry.EntryType.Event;
      case "fallback":
        return SmartContract.ABI.Entry.EntryType.Fallback;
      default:
        return SmartContract.ABI.Entry.EntryType.UNRECOGNIZED;
    }
  }
  /**
   * constructor.
   */

  public static SmartContract.ABI.Entry.StateMutabilityType getStateMutability(
      String stateMutability) {
    switch (stateMutability) {
      case "pure":
        return SmartContract.ABI.Entry.StateMutabilityType.Pure;
      case "view":
        return SmartContract.ABI.Entry.StateMutabilityType.View;
      case "nonpayable":
        return SmartContract.ABI.Entry.StateMutabilityType.Nonpayable;
      case "payable":
        return SmartContract.ABI.Entry.StateMutabilityType.Payable;
      default:
        return SmartContract.ABI.Entry.StateMutabilityType.UNRECOGNIZED;
    }
  }
  /**
   * constructor.
   */

  public static byte[] generateContractAddress(Transaction trx, byte[] owneraddress) {

    // get owner address
    // this address should be as same as the onweraddress in trx, DONNOT modify it
    byte[] ownerAddress = owneraddress;

    // get tx hash
    byte[] txRawDataHash = Sha256Hash.of(trx.getRawData().toByteArray()).getBytes();

    // combine
    byte[] combined = new byte[txRawDataHash.length + ownerAddress.length];
    System.arraycopy(txRawDataHash, 0, combined, 0, txRawDataHash.length);
    System.arraycopy(ownerAddress, 0, combined, txRawDataHash.length, ownerAddress.length);

    return Hash.sha3omit12(combined);

  }
  /**
   * constructor.
   */

  public static SmartContract getContract(byte[] address, WalletGrpc
      .WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ByteString byteString = ByteString.copyFrom(address);
    BytesMessage bytesMessage = BytesMessage.newBuilder().setValue(byteString).build();
    Integer i = 0;
    while (blockingStubFull.getContract(bytesMessage).getName().isEmpty() && i++ < 4) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    logger.info("contract name is " + blockingStubFull.getContract(bytesMessage).getName());
    logger.info("contract address is " + WalletClient.encode58Check(address));
    return blockingStubFull.getContract(bytesMessage);
  }

  private static byte[] replaceLibraryAddress(String code, String libraryAddressPair) {

    String[] libraryAddressList = libraryAddressPair.split("[,]");

    for (int i = 0; i < libraryAddressList.length; i++) {
      String cur = libraryAddressList[i];

      int lastPosition = cur.lastIndexOf(":");
      if (-1 == lastPosition) {
        throw new RuntimeException("libraryAddress delimit by ':'");
      }
      String libraryName = cur.substring(0, lastPosition);
      String addr = cur.substring(lastPosition + 1);
      String libraryAddressHex = ByteArray.toHexString(Wallet.decodeFromBase58Check(addr))
          .substring(2);

      String repeated = new String(new char[40 - libraryName.length() - 2]).replace("\0", "_");
      String beReplaced = "__" + libraryName + repeated;
      Matcher m = Pattern.compile(beReplaced).matcher(code);
      code = m.replaceAll(libraryAddressHex);
    }

    return Hex.decode(code);
  }

  private static byte[] replaceLibraryAddress_1(String code, byte[] libraryAddress) {

    String libraryAddressHex = ByteArray.toHexString(libraryAddress).substring(2);

    Matcher m = Pattern.compile("__.*__").matcher(code);
    code = m.replaceAll(libraryAddressHex);
    return Hex.decode(code);
  }
  /**
   * constructor.
   */

  public static boolean updateSetting(byte[] contractAddress, long consumeUserResourcePercent,
      String priKey, byte[] ownerAddress, WalletGrpc
      .WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    byte[] owner = ownerAddress;
    UpdateSettingContract.Builder builder = UpdateSettingContract.newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(owner));
    builder.setContractAddress(ByteString.copyFrom(contractAddress));
    builder.setConsumeUserResourcePercent(consumeUserResourcePercent);

    UpdateSettingContract updateSettingContract = builder.build();
    TransactionExtention transactionExtention = blockingStubFull
        .updateSetting(updateSettingContract);
    if (transactionExtention == null || !transactionExtention.getResult().getResult()) {
      System.out.println("RPC create trx failed!");
      if (transactionExtention != null) {
        System.out.println("Code = " + transactionExtention.getResult().getCode());
        System.out
            .println("Message = " + transactionExtention.getResult().getMessage().toStringUtf8());
      }
      return false;
    }
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
    transaction = signTransaction(ecKey, transaction);
    Return response = blockingStubFull.broadcastTransaction(transaction);
    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    } else {
      return true;
    }
  }
  /**
   * constructor.
   */

  public static Optional<TransactionInfo> getTransactionInfoById(String txId, WalletGrpc
      .WalletBlockingStub blockingStubFull) {
    ByteString bsTxid = ByteString.copyFrom(ByteArray.fromHexString(txId));
    BytesMessage request = BytesMessage.newBuilder().setValue(bsTxid).build();
    TransactionInfo transactionInfo;
    transactionInfo = blockingStubFull.getTransactionInfoById(request);
    return Optional.ofNullable(transactionInfo);
  }

  public static String triggerContract(byte[] contractAddress, String method, String argsStr,
      Boolean isHex, long callValue, long feeLimit, byte[] ownerAddress,
      String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    return triggerContract(contractAddress, method, argsStr, isHex, callValue, feeLimit,
        "0", 0, ownerAddress, priKey, blockingStubFull);
  }
  /**
   * constructor.
   */

  public static String triggerContract(byte[] contractAddress, String method, String argsStr,
      Boolean isHex, long callValue, long feeLimit, String tokenId, long tokenValue,
      byte[] ownerAddress,
      String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;
    if (argsStr.equalsIgnoreCase("#")) {
      logger.info("argsstr is #");
      argsStr = "";
    }

    byte[] owner = ownerAddress;
    byte[] input = Hex.decode(AbiUtil.parseMethod(method, argsStr, isHex));

    Contract.TriggerSmartContract.Builder builder = Contract.TriggerSmartContract.newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(owner));
    builder.setContractAddress(ByteString.copyFrom(contractAddress));
    builder.setData(ByteString.copyFrom(input));
    builder.setCallValue(callValue);
    builder.setTokenId(Long.parseLong(tokenId));
    builder.setCallTokenValue(tokenValue);
    Contract.TriggerSmartContract triggerContract = builder.build();

    TransactionExtention transactionExtention = blockingStubFull.triggerContract(triggerContract);
    if (transactionExtention == null || !transactionExtention.getResult().getResult()) {
      System.out.println("RPC create call trx failed!");
      System.out.println("Code = " + transactionExtention.getResult().getCode());
      System.out
          .println("Message = " + transactionExtention.getResult().getMessage().toStringUtf8());
      return null;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction.getRetCount() != 0
        && transactionExtention.getConstantResult(0) != null
        && transactionExtention.getResult() != null) {
      byte[] result = transactionExtention.getConstantResult(0).toByteArray();
      System.out.println("message:" + transaction.getRet(0).getRet());
      System.out.println(":" + ByteArray
          .toStr(transactionExtention.getResult().getMessage().toByteArray()));
      System.out.println("Result:" + Hex.toHexString(result));
      return null;
    }

    final TransactionExtention.Builder texBuilder = TransactionExtention.newBuilder();
    Transaction.Builder transBuilder = Transaction.newBuilder();
    Transaction.raw.Builder rawBuilder = transactionExtention.getTransaction().getRawData()
        .toBuilder();
    rawBuilder.setFeeLimit(feeLimit);
    transBuilder.setRawData(rawBuilder);
    for (int i = 0; i < transactionExtention.getTransaction().getSignatureCount(); i++) {
      ByteString s = transactionExtention.getTransaction().getSignature(i);
      transBuilder.setSignature(i, s);
    }
    for (int i = 0; i < transactionExtention.getTransaction().getRetCount(); i++) {
      Result r = transactionExtention.getTransaction().getRet(i);
      transBuilder.setRet(i, r);
    }
    texBuilder.setTransaction(transBuilder);
    texBuilder.setResult(transactionExtention.getResult());
    texBuilder.setTxid(transactionExtention.getTxid());
    transactionExtention = texBuilder.build();
    if (transactionExtention == null) {
      return null;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return null;
    }
    transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return null;
    }
    transaction = signTransaction(ecKey, transaction);
    System.out.println(
        "trigger txid = " + ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData()
            .toByteArray())));
    int i = 10;
    Return response = blockingStubFull.broadcastTransaction(transaction);
    while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
        && i > 0) {
      i--;
      response = blockingStubFull.broadcastTransaction(transaction);
      logger.info("repeate times = " + (11 - i));
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (response.getResult() == false) {
      logger.info("Code = " + response.getCode());
      logger.info("Message = " + response.getMessage().toStringUtf8());
      return null;
    } else {
      //logger.info("brodacast succesfully");
      return ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray()));
    }
  }
  /**
   * constructor.
   */

  public static Boolean exchangeCreate(byte[] firstTokenId, long firstTokenBalance,
      byte[] secondTokenId, long secondTokenBalance, byte[] ownerAddress,
      String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    byte[] owner = ownerAddress;

    Contract.ExchangeCreateContract.Builder builder = Contract.ExchangeCreateContract.newBuilder();
    builder
        .setOwnerAddress(ByteString.copyFrom(owner))
        .setFirstTokenId(ByteString.copyFrom(firstTokenId))
        .setFirstTokenBalance(firstTokenBalance)
        .setSecondTokenId(ByteString.copyFrom(secondTokenId))
        .setSecondTokenBalance(secondTokenBalance);
    Contract.ExchangeCreateContract contract = builder.build();
    TransactionExtention transactionExtention = blockingStubFull.exchangeCreate(contract);
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
    transaction = signTransaction(ecKey, transaction);
    System.out.println(
        "txid = " + ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray())));

    int i = 10;
    Return response = blockingStubFull.broadcastTransaction(transaction);
    while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
        && i > 0) {
      i--;
      response = blockingStubFull.broadcastTransaction(transaction);
      logger.info("repeate times = " + (11 - i));
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (response.getResult() == false) {
      logger.info("Code = " + response.getCode());
      logger.info("Message = " + response.getMessage().toStringUtf8());
    }
    return response.getResult();
  }
  /**
   * constructor.
   */

  public static Boolean injectExchange(long exchangeId, byte[] tokenId, long quant,
      byte[] ownerAddress, String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    byte[] owner = ownerAddress;

    Contract.ExchangeInjectContract.Builder builder = Contract.ExchangeInjectContract.newBuilder();
    builder
        .setOwnerAddress(ByteString.copyFrom(owner))
        .setExchangeId(exchangeId)
        .setTokenId(ByteString.copyFrom(tokenId))
        .setQuant(quant);
    Contract.ExchangeInjectContract contract = builder.build();
    TransactionExtention transactionExtention = blockingStubFull.exchangeInject(contract);
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
    transaction = signTransaction(ecKey, transaction);
    System.out.println(
        "txid = " + ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray())));
    int i = 10;
    Return response = blockingStubFull.broadcastTransaction(transaction);
    while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
        && i > 0) {
      i--;
      response = blockingStubFull.broadcastTransaction(transaction);
      logger.info("repeate times = " + (11 - i));
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (response.getResult() == false) {
      logger.info("Code = " + response.getCode());
      logger.info("Message = " + response.getMessage().toStringUtf8());
    }
    return response.getResult();
  }

  public static Optional<ExchangeList> getExchangeList(WalletGrpc.WalletBlockingStub
      blockingStubFull) {
    ExchangeList exchangeList = blockingStubFull.listExchanges(EmptyMessage.newBuilder().build());
    return Optional.ofNullable(exchangeList);
  }
  /**
   * constructor.
   */

  public static Optional<ExchangeList> getExchangeList(WalletSolidityGrpc
      .WalletSolidityBlockingStub blockingStubSolidity) {
    ExchangeList exchangeList = blockingStubSolidity.listExchanges(EmptyMessage.newBuilder()
        .build());
    return Optional.ofNullable(exchangeList);
  }


  /**
   * constructor.
   */

  public static Optional<Exchange> getExchange(String id, WalletSolidityGrpc
      .WalletSolidityBlockingStub blockingStubSolidity) {
    BytesMessage request = BytesMessage.newBuilder().setValue(ByteString.copyFrom(
        ByteArray.fromLong(Long.parseLong(id))))
        .build();
    Exchange exchange = blockingStubSolidity.getExchangeById(request);
    return Optional.ofNullable(exchange);
  }
  /**
   * constructor.
   */

  public static Optional<Exchange> getExchange(String id, WalletGrpc.WalletBlockingStub
      blockingStubFull) {
    BytesMessage request = BytesMessage.newBuilder().setValue(ByteString.copyFrom(
        ByteArray.fromLong(Long.parseLong(id))))
        .build();
    Exchange exchange = blockingStubFull.getExchangeById(request);
    return Optional.ofNullable(exchange);
  }

  /**
   * constructor.
   */

  public static boolean exchangeWithdraw(long exchangeId, byte[] tokenId, long quant,
      byte[] ownerAddress, String priKey, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;
    byte[] owner = ownerAddress;

    Contract.ExchangeWithdrawContract.Builder builder = Contract.ExchangeWithdrawContract
        .newBuilder();
    builder
        .setOwnerAddress(ByteString.copyFrom(owner))
        .setExchangeId(exchangeId)
        .setTokenId(ByteString.copyFrom(tokenId))
        .setQuant(quant);
    Contract.ExchangeWithdrawContract contract = builder.build();
    TransactionExtention transactionExtention = blockingStubFull.exchangeWithdraw(contract);
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
    transaction = signTransaction(ecKey, transaction);
    System.out.println(
        "txid = " + ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray())));

    int i = 10;
    Return response = blockingStubFull.broadcastTransaction(transaction);
    while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
        && i > 0) {
      i--;
      response = blockingStubFull.broadcastTransaction(transaction);
      logger.info("repeate times = " + (11 - i));
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (response.getResult() == false) {
      logger.info("Code = " + response.getCode());
      logger.info("Message = " + response.getMessage().toStringUtf8());
    }
    return response.getResult();
  }

  /**
   * constructor.
   */

  public static boolean exchangeTransaction(long exchangeId, byte[] tokenId, long quant,
      long expected, byte[] ownerAddress, String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;
    byte[] owner = ownerAddress;

    Contract.ExchangeTransactionContract.Builder builder = Contract.ExchangeTransactionContract
        .newBuilder();
    builder
        .setOwnerAddress(ByteString.copyFrom(owner))
        .setExchangeId(exchangeId)
        .setTokenId(ByteString.copyFrom(tokenId))
        .setQuant(quant)
        .setExpected(expected);
    Contract.ExchangeTransactionContract contract = builder.build();
    TransactionExtention transactionExtention = blockingStubFull.exchangeTransaction(contract);
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
    transaction = signTransaction(ecKey, transaction);
    System.out.println(
        "txid = " + ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray())));

    int i = 10;
    Return response = blockingStubFull.broadcastTransaction(transaction);
    while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
        && i > 0) {
      i--;
      response = blockingStubFull.broadcastTransaction(transaction);
      logger.info("repeate times = " + (11 - i));
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (response.getResult() == false) {
      logger.info("Code = " + response.getCode());
      logger.info("Message = " + response.getMessage().toStringUtf8());
    }
    return response.getResult();
  }

  /**
   * constructor.
   */

  public static String deployContractWithConstantParame(String contractName, String abiString,
      String code, String constructorStr, String argsStr, String data, Long feeLimit, long value,
      long consumeUserResourcePercent, String libraryAddress, String priKey, byte[] ownerAddress,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    return deployContractWithConstantParame(contractName, abiString, code, constructorStr, argsStr,
        data, feeLimit, value, consumeUserResourcePercent, 1000L, "0", 0L,
        libraryAddress, priKey, ownerAddress, blockingStubFull);
  }
  /**
   * constructor.
   */

  public static String deployContractWithConstantParame(String contractName, String abiString,
      String code, String constructorStr, String argsStr, String data, Long feeLimit, long value,
      long consumeUserResourcePercent, long originEnergyLimit, String tokenId, long tokenValue,
      String libraryAddress, String priKey, byte[] ownerAddress,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    SmartContract.ABI abi = jsonStr2Abi(abiString);
    if (abi == null) {
      logger.error("abi is null");
      return null;
    }

    code += Hex.toHexString(AbiUtil.encodeInput(constructorStr, argsStr));
    byte[] owner = ownerAddress;
    SmartContract.Builder builder = SmartContract.newBuilder();
    builder.setName(contractName);
    builder.setOriginAddress(ByteString.copyFrom(owner));
    builder.setAbi(abi);
    builder.setConsumeUserResourcePercent(consumeUserResourcePercent);
    builder.setOriginEnergyLimit(originEnergyLimit);

    if (value != 0) {

      builder.setCallValue(value);
    }

    byte[] byteCode;
    if (null != libraryAddress) {
      byteCode = replaceLibraryAddress(code, libraryAddress);
    } else {
      byteCode = Hex.decode(code);
    }
    builder.setBytecode(ByteString.copyFrom(byteCode));

    Builder contractBuilder = CreateSmartContract.newBuilder();
    contractBuilder.setOwnerAddress(ByteString.copyFrom(owner));
    contractBuilder.setCallTokenValue(tokenValue);
    contractBuilder.setTokenId(Long.parseLong(tokenId));
    CreateSmartContract contractDeployContract = contractBuilder.setNewContract(builder.build())
        .build();

    TransactionExtention transactionExtention = blockingStubFull
        .deployContract(contractDeployContract);
    if (transactionExtention == null || !transactionExtention.getResult().getResult()) {
      System.out.println("RPC create trx failed!");
      if (transactionExtention != null) {
        System.out.println("Code = " + transactionExtention.getResult().getCode());
        System.out
            .println("Message = " + transactionExtention.getResult().getMessage().toStringUtf8());
      }
      return null;
    }

    final TransactionExtention.Builder texBuilder = TransactionExtention.newBuilder();
    Transaction.Builder transBuilder = Transaction.newBuilder();
    Transaction.raw.Builder rawBuilder = transactionExtention.getTransaction().getRawData()
        .toBuilder();
    rawBuilder.setFeeLimit(feeLimit);
    transBuilder.setRawData(rawBuilder);
    for (int i = 0; i < transactionExtention.getTransaction().getSignatureCount(); i++) {
      ByteString s = transactionExtention.getTransaction().getSignature(i);
      transBuilder.setSignature(i, s);
    }
    for (int i = 0; i < transactionExtention.getTransaction().getRetCount(); i++) {
      Result r = transactionExtention.getTransaction().getRet(i);
      transBuilder.setRet(i, r);
    }
    texBuilder.setTransaction(transBuilder);
    texBuilder.setResult(transactionExtention.getResult());
    texBuilder.setTxid(transactionExtention.getTxid());
    transactionExtention = texBuilder.build();

    if (transactionExtention == null) {
      return null;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return null;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return null;
    }
    transaction = signTransaction(ecKey, transaction);
    System.out.println(
        "txid = " + ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray())));
    byte[] contractAddress = generateContractAddress(transaction, owner);
    System.out.println(
        "Your smart contract address will be: " + WalletClient.encode58Check(contractAddress));
    int i = 10;
    Return response = blockingStubFull.broadcastTransaction(transaction);
    while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
        && i > 0) {
      i--;
      response = blockingStubFull.broadcastTransaction(transaction);
      logger.info("repeate times = " + (11 - i));
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (response.getResult() == false) {
      logger.info("Code = " + response.getCode());
      logger.info("Message = " + response.getMessage().toStringUtf8());
      return null;
    } else {
      //logger.info("brodacast succesfully");
      return ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray()));
    }
  }
  /**
   * constructor.
   */

  public static Boolean freezeBalanceForReceiver(byte[] addRess, long freezeBalance,
      long freezeDuration, int resourceCode, ByteString receiverAddressBytes,String priKey,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    byte[] address = addRess;
    long frozenBalance = freezeBalance;
    long frozenDuration = freezeDuration;
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    final ECKey ecKey = temKey;

    Contract.FreezeBalanceContract.Builder builder = Contract.FreezeBalanceContract.newBuilder();
    ByteString byteAddreess = ByteString.copyFrom(address);

    builder.setOwnerAddress(byteAddreess).setFrozenBalance(frozenBalance)
        .setFrozenDuration(frozenDuration).setResourceValue(resourceCode);
    builder.setReceiverAddress(receiverAddressBytes);
    Contract.FreezeBalanceContract contract = builder.build();
    Transaction transaction = blockingStubFull.freezeBalance(contract);

    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      logger.info("transaction = null");
      return false;
    }
    transaction = TransactionUtils.setTimestamp(transaction);
    transaction = TransactionUtils.sign(transaction, ecKey);
    Return response = blockingStubFull.broadcastTransaction(transaction);

    if (response.getResult() == false) {
      logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
      return false;
    }
    return true;
  }
  /**
   * constructor.
   */

  public static Optional<DelegatedResourceList> getDelegatedResource(byte[] fromAddress,
      byte[] toAddress, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ByteString fromAddressBs = ByteString.copyFrom(fromAddress);
    ByteString toAddressBs = ByteString.copyFrom(toAddress);

    DelegatedResourceMessage request = DelegatedResourceMessage.newBuilder()
        .setFromAddress(fromAddressBs)
        .setToAddress(toAddressBs)
        .build();
    DelegatedResourceList delegatedResource = blockingStubFull.getDelegatedResource(request);
    return Optional.ofNullable(delegatedResource);
  }
  /**
   * constructor.
   */

  public static Optional<DelegatedResourceAccountIndex> getDelegatedResourceAccountIndex(
      byte[] address, WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);

    ByteString addressBs = ByteString.copyFrom(address);

    BytesMessage bytesMessage = BytesMessage.newBuilder().setValue(addressBs).build();

    DelegatedResourceAccountIndex accountIndex = blockingStubFull
        .getDelegatedResourceAccountIndex(bytesMessage);
    return Optional.ofNullable(accountIndex);
  }
  /**
   * constructor.
   */

  public static Contract.AssetIssueContract getAssetIssueByName(String assetName,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ByteString assetNameBs = ByteString.copyFrom(assetName.getBytes());
    BytesMessage request = BytesMessage.newBuilder().setValue(assetNameBs).build();
    return blockingStubFull.getAssetIssueByName(request);
  }
  /**
   * constructor.
   */

  public static Optional<AssetIssueList> getAssetIssueListByName(String assetName,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ByteString assetNameBs = ByteString.copyFrom(assetName.getBytes());
    BytesMessage request = BytesMessage.newBuilder().setValue(assetNameBs).build();
    AssetIssueList assetIssueList = blockingStubFull.getAssetIssueListByName(request);
    return Optional.ofNullable(assetIssueList);
  }
  /**
   * constructor.
   */

  public static Contract.AssetIssueContract getAssetIssueById(String assetId,
      WalletGrpc.WalletBlockingStub blockingStubFull) {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ByteString assetIdBs = ByteString.copyFrom(assetId.getBytes());
    BytesMessage request = BytesMessage.newBuilder().setValue(assetIdBs).build();
    return blockingStubFull.getAssetIssueById(request);
  }

  public static boolean permissionAddKey(String permission, byte[] addAddress, int weight, byte[] owner,String priKey,WalletGrpc.WalletBlockingStub blockingStubFull)
       {
         Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
         ECKey temKey = null;
         try {
           BigInteger priK = new BigInteger(priKey, 16);
           temKey = ECKey.fromPrivate(priK);
         } catch (Exception ex) {
           ex.printStackTrace();
         }
         ECKey ecKey = temKey;
    Contract.PermissionAddKeyContract.Builder contractBuilder =
        Contract.PermissionAddKeyContract.newBuilder();
    contractBuilder.setOwnerAddress(ByteString.copyFrom(owner));
    contractBuilder.setPermissionName(permission);
    Key.Builder keyBuilder = Key.newBuilder();
    keyBuilder.setAddress(ByteString.copyFrom(addAddress));
    keyBuilder.setWeight(weight);
    contractBuilder.setKey(keyBuilder.build());

    Contract.PermissionAddKeyContract permissionAddKeyContract =contractBuilder.build();
    TransactionExtention transactionExtention = blockingStubFull.permissionAddKey(permissionAddKeyContract);
         if (transactionExtention == null) {
           return false;
         }
         Return ret = transactionExtention.getResult();
         if (!ret.getResult()) {
           System.out.println("Code = " + ret.getCode());
           System.out.println("Message = " + ret.getMessage().toStringUtf8());
           return false;
         }
         Transaction transaction = transactionExtention.getTransaction();
         if (transaction == null || transaction.getRawData().getContractCount() == 0) {
           System.out.println("Transaction is empty");
           return false;
         }
         System.out.println(
             "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
         transaction = signTransaction(ecKey,transaction);
         int i = 10;
         Return response = blockingStubFull.broadcastTransaction(transaction);
         while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
             && i > 0) {
           i--;
           response = blockingStubFull.broadcastTransaction(transaction);
           logger.info("repeat times = " + (11 - i));
           try {
             Thread.sleep(300);
           } catch (InterruptedException e) {
             e.printStackTrace();
           }
         }
         if (response.getResult() == false) {
           logger.info("Code = " + response.getCode());
           logger.info("Message = " + response.getMessage().toStringUtf8());
         }
         return response.getResult();
  }



  public static boolean permissionUpdateKey(String permission, byte[] updateAddress, int weight, byte[] owner,String priKey,WalletGrpc.WalletBlockingStub blockingStubFull)
  {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    ECKey ecKey = temKey;
    Contract.PermissionUpdateKeyContract.Builder contractBuilder =
        Contract.PermissionUpdateKeyContract.newBuilder();
    contractBuilder.setOwnerAddress(ByteString.copyFrom(owner));
    contractBuilder.setPermissionName(permission);
    Key.Builder keyBuilder = Key.newBuilder();
    keyBuilder.setAddress(ByteString.copyFrom(updateAddress));
    keyBuilder.setWeight(weight);
    contractBuilder.setKey(keyBuilder.build());

    Contract.PermissionUpdateKeyContract permissionUpdateKeyContract =contractBuilder.build();
    TransactionExtention transactionExtention = blockingStubFull.permissionUpdateKey(permissionUpdateKeyContract);
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
    transaction = signTransaction(ecKey,transaction);
    int i = 10;
    Return response = blockingStubFull.broadcastTransaction(transaction);
    while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
        && i > 0) {
      i--;
      response = blockingStubFull.broadcastTransaction(transaction);
      logger.info("repeat times = " + (11 - i));
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (response.getResult() == false) {
      logger.info("Code = " + response.getCode());
      logger.info("Message = " + response.getMessage().toStringUtf8());
    }
    return response.getResult();
  }

  public static boolean permissionDeleteKey(String permission, byte[] deleteAddress, byte[] owner,String priKey,WalletGrpc.WalletBlockingStub blockingStubFull)
  {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    ECKey ecKey = temKey;
    Contract.PermissionDeleteKeyContract.Builder contractBuilder =
        Contract.PermissionDeleteKeyContract.newBuilder();
    contractBuilder.setOwnerAddress(ByteString.copyFrom(owner));
    contractBuilder.setPermissionName(permission);
    contractBuilder.setKeyAddress(ByteString.copyFrom(deleteAddress));
    Contract.PermissionDeleteKeyContract permissionDeleteKeyContract =
      contractBuilder.build();
    TransactionExtention transactionExtention = blockingStubFull.permissionDeleteKey(permissionDeleteKeyContract);
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));
    transaction = signTransaction(ecKey,transaction);
    int i = 10;
    Return response = blockingStubFull.broadcastTransaction(transaction);
    while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
        && i > 0) {
      i--;
      response = blockingStubFull.broadcastTransaction(transaction);
      logger.info("repeat times = " + (11 - i));
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (response.getResult() == false) {
      logger.info("Code = " + response.getCode());
      logger.info("Message = " + response.getMessage().toStringUtf8());
    }
    return response.getResult();
  }

  public static boolean accountPermissionUpdate(String permissionJson, byte[] owner,String priKey,WalletGrpc.WalletBlockingStub blockingStubFull,String[] priKeys)
  {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    ECKey ecKey = temKey;

    Contract.AccountPermissionUpdateContract.Builder builder =
        Contract.AccountPermissionUpdateContract.newBuilder();

    JSONArray permissions = JSON.parseArray(permissionJson);
    List<Permission> permissionList = new ArrayList<>();
    for (int j = 0; j < permissions.size(); j++) {
      Permission.Builder permissionBuilder = Permission.newBuilder();
      JSONObject permission = permissions.getJSONObject(j);
      String name = permission.getString("name");
      String parent = permission.getString("parent");
      int threshold = Integer.parseInt(permission.getString("threshold"));
      JSONArray keys = permission.getJSONArray("keys");
      List<Key> keyList = new ArrayList<>();
      for (int i = 0; i < keys.size(); i++) {
        Key.Builder keyBuilder = Key.newBuilder();
        JSONObject key = keys.getJSONObject(i);
        String address = key.getString("address");
        int weight = key.getInteger("weight");
        keyBuilder.setAddress(ByteString.copyFrom(Wallet.decodeFromBase58Check(address)));
        keyBuilder.setWeight(weight);
        keyList.add(keyBuilder.build());
      }
      permissionBuilder.setName(name);
      if (!(name.equals("owner") && parent == null)) {
        permissionBuilder.setParent(parent);
      }
      permissionBuilder.setThreshold(threshold);
      permissionBuilder.addAllKeys(keyList);
      permissionList.add(permissionBuilder.build());
    }

    builder.setOwnerAddress(ByteString.copyFrom(owner));
    builder.addAllPermissions(permissionList);
    Contract.AccountPermissionUpdateContract contract = builder.build();

    TransactionExtention transactionExtention = blockingStubFull.accountPermissionUpdate(contract);
    if (transactionExtention == null) {
      return false;
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      System.out.println("Code = " + ret.getCode());
      System.out.println("Message = " + ret.getMessage().toStringUtf8());
      return false;
    }
    Transaction transaction = transactionExtention.getTransaction();
    if (transaction == null || transaction.getRawData().getContractCount() == 0) {
      System.out.println("Transaction is empty");
      return false;
    }
    System.out.println(
        "Receive txid = " + ByteArray.toHexString(transactionExtention.getTxid().toByteArray()));

    transaction = signTransaction(transaction,blockingStubFull,priKeys);
    int i = 10;
    Return response = blockingStubFull.broadcastTransaction(transaction);
    while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
        && i > 0) {
      i--;
      response = blockingStubFull.broadcastTransaction(transaction);
      logger.info("repeat times = " + (11 - i));
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (response.getResult() == false) {
      logger.info("Code = " + response.getCode());
      logger.info("Message = " + response.getMessage().toStringUtf8());
    }
    return response.getResult();
  }


  public Transaction addTransactionSign(Transaction transaction, String priKey,WalletGrpc.WalletBlockingStub blockingStubFull)
  {
    Wallet.setAddressPreFixByte(CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    ECKey temKey = null;
    try {
      BigInteger priK = new BigInteger(priKey, 16);
      temKey = ECKey.fromPrivate(priK);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    ECKey ecKey = temKey;

    Transaction.Builder transactionBuilderSigned = transaction.toBuilder();
    byte[] hash = Sha256Hash.hash(transaction.getRawData().toByteArray());

    ECDSASignature signature = ecKey.sign(hash);
    ByteString bsSign = ByteString.copyFrom(signature.toByteArray());
    transactionBuilderSigned.addSignature(bsSign);
    transaction = transactionBuilderSigned.build();
    return transaction;
  }


  private static Transaction signTransaction(Transaction transaction,WalletGrpc.WalletBlockingStub blockingStubFull,String[] priKeys)
       {
    if (transaction.getRawData().getTimestamp() == 0) {
      transaction = TransactionUtils.setTimestamp(transaction);
    }

         long currentTime = System.currentTimeMillis();//*1000000 + System.nanoTime()%1000000;
         Transaction.Builder builder = transaction.toBuilder();
         org.tron.protos.Protocol.Transaction.raw.Builder rowBuilder = transaction.getRawData()
             .toBuilder();
         rowBuilder.setTimestamp(currentTime);
         builder.setRawData(rowBuilder.build());
    transaction = builder.build();

         for (int i = 0; i < priKeys.length; i += 1) {
           String priKey = priKeys[i];
           ECKey temKey = null;
           try {
             BigInteger priK = new BigInteger(priKey, 16);
             temKey = ECKey.fromPrivate(priK);
           } catch (Exception ex) {
             ex.printStackTrace();
           }
           ECKey ecKey = temKey;

           transaction = TransactionUtils.sign(transaction, ecKey);
           TransactionSignWeight weight = blockingStubFull.getTransactionSignWeight(transaction);
           if (weight.getResult().getCode() == TransactionSignWeight.Result.response_code.ENOUGH_PERMISSION) {
             break;
           }
           if (weight.getResult().getCode() == TransactionSignWeight.Result.response_code.NOT_ENOUGH_PERMISSION) {
             continue;
           }

         }
    return transaction;
  }
}