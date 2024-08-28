// 请求地址
const apiHostUrl = process.env.API_HOST_URL ? process.env.API_HOST_URL : "https://console-mock.apipost.cn/mock/6afa907d-6678-45e2-b867-032a11090abd";

/**
 * 查询抽奖奖品列表
 * @param strategyId 策略ID
 */
export const queryRaffleAwardList = (strategyId: number) => {
    try {
        return fetch(`${apiHostUrl}/api/v1/raffle/query_raffle_award_list?strategyId=${strategyId}?apipost_id=0de1ab`, {
            method: 'get',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            }
        });
    } catch (error) {
        return fetch("{\n" +
            "    \"code\": \"0001\",\n" +
            "    \"info\": \"调用失败\",\n" +
            "    \"data\": [\n" +
            "}");
    }
}


export const randomRaffle = (strategyId: number) => {
    try {
        return fetch(`${apiHostUrl}/api/v1/raffle/random_raffle?strategyId=${strategyId}?apipost_id=99dd37`, {
            method: 'get',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            }
        })
    } catch (error) {
        return fetch("{\n" +
            "    \"code\": \"0001\",\n" +
            "    \"info\": \"调用失败\",\n" +
            "    \"data\": [\n" +
            "}");
    }
}