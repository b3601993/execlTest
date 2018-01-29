package sshTest.kex;

import sshTest.tran.Buffer;
import sshTest.tran.Session;
import sshTest.utils.Utils;

public abstract class KeyExchange {

	public static final int PROPOSAL_KEX_ALGS = 0;
	public static final int PROPOSAL_ENC_ALGS_CTOS = 2;
	public static final int PROPOSAL_ENC_ALGS_STOC = 3;
	public static final int PROPOSAL_MAX = 10;
	
	
	public abstract void init(Session session, byte[] V_S, byte[] V_C, byte[] I_S, byte[] I_C) throws Exception;
	
	public abstract int getState();

	public abstract boolean next(Buffer buf);
	
	
	/**
	 * 猜测算法（协商算法），优先使用数组中的第一个
	 * @param I_S
	 * @param I_C
	 * @return
	 * @author yutao
	 * @date 2018年1月29日下午4:49:13
	 */
	public static String[] guess(byte[] I_S, byte[] I_C) {
		String[] guess = new String[PROPOSAL_MAX];
		Buffer sb = new Buffer(I_S);
		sb.setOffSet(17);
		Buffer cb = new Buffer(I_C);
		cb.setOffSet(17);

		/*if (JSch.getLogger().isEnabled(Logger.INFO)) {
			for (int i = 0; i < PROPOSAL_MAX; i++) {
				JSch.getLogger().log(Logger.INFO, "kex: server: " + Util.byte2str(sb.getString()));
			}
			for (int i = 0; i < PROPOSAL_MAX; i++) {
				JSch.getLogger().log(Logger.INFO, "kex: client: " + Util.byte2str(cb.getString()));
			}
			sb.setOffSet(17);
			cb.setOffSet(17);
		}*/

		for (int i = 0; i < PROPOSAL_MAX; i++) {
			byte[] sp = sb.getString(); // server proposal
			byte[] cp = cb.getString(); // client proposal
			int j = 0;
			int k = 0;

			loop: while (j < cp.length) {
				while (j < cp.length && cp[j] != ',')
					j++;
				if (k == j)
					return null;
				String algorithm = Utils.byte2str(cp, k, j - k);
				int l = 0;
				int m = 0;
				while (l < sp.length) {
					while (l < sp.length && sp[l] != ',')
						l++;
					if (m == l)
						return null;
					if (algorithm.equals(Utils.byte2str(sp, m, l - m))) {
						guess[i] = algorithm;
						break loop;
					}
					l++;
					m = l;
				}
				j++;
				k = j;
			}
			if (j == 0) {
				guess[i] = "";
			} else if (guess[i] == null) {
				return null;
			}
		}

		/*if (JSch.getLogger().isEnabled(Logger.INFO)) {
			JSch.getLogger().log(Logger.INFO, "kex: server->client" + " " + guess[PROPOSAL_ENC_ALGS_STOC] + " "
					+ guess[PROPOSAL_MAC_ALGS_STOC] + " " + guess[PROPOSAL_COMP_ALGS_STOC]);
			JSch.getLogger().log(Logger.INFO, "kex: client->server" + " " + guess[PROPOSAL_ENC_ALGS_CTOS] + " "
					+ guess[PROPOSAL_MAC_ALGS_CTOS] + " " + guess[PROPOSAL_COMP_ALGS_CTOS]);
		}*/

		return guess;
	}

}
