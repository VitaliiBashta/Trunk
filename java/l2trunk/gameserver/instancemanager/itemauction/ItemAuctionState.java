/*	*/ package l2trunk.gameserver.instancemanager.itemauction;
/*	*/ 
/*	*/ import l2trunk.commons.lang.ArrayUtils;
/*	*/ 
/*	*/ public enum ItemAuctionState
/*	*/ {
/*  5 */   CREATED, STARTED, FINISHED;
/*	*/ 
/*	*/   public static ItemAuctionState stateForStateId(int stateId)
/*	*/   {
/* 13 */	return ((ItemAuctionState)ArrayUtils.valid(values(), stateId));
/*	*/   }
/*	*/ }