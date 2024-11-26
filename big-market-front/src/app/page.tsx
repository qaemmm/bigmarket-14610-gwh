"use client";

import {LuckyWheelPage} from "@/app/pages/lucky/lucky-wheel-page";
import {LuckyGridPage} from "@/app/pages/lucky/lucky-grid-page";
import dynamic from "next/dynamic";
import {useState} from "react";

const StrategyArmoryButton = dynamic(async () => (await import("./components/StrategyArmory")).StrategyArmory)
const ActivityAccountButton = dynamic(async () => (await import("./components/ActivityAccount")).ActivityAccount)
const CalendarSignButton = dynamic(async () => (await import("./components/CalendarSign")).CalendarSign)
const StrategyRuleWeightButton = dynamic(async () => (await import("./components/StrategyRuleWeight")).StrategyRuleWeight)


export default function Home() {

    const [refresh, setRefresh] = useState(0);

    const handleRefresh = () => {
        setRefresh(refresh + 1)
    };


    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100"
             style={{backgroundImage: "url('/background.svg')", backgroundColor: "#e7305e"}}>
            {/* 头部文案 */}
            <header className="text-7xl font-bold text-center text-gray-800 my-8" style={{color: "white"}}>
                大营销平台 - 抽奖展示
            </header>


            <div className="flex items-center space-x-4">
                {/* 装配抽奖 */}
                <StrategyArmoryButton/>

                {/* 账户额度 */}
                <ActivityAccountButton refresh={refresh}/>

                {/* 日历签到 */}
                <CalendarSignButton handleRefresh={handleRefresh}/>
            </div>

            {/* 中间的两个div元素 */}
            <div className="flex flex-col md:flex-row gap-4 mb-8">
                <div className="w-full md:w-1/2 p-6 bg-white shadow-lg rounded-lg">
                    <div className="text-gray-700">
                        <LuckyWheelPage/>
                    </div>
                </div>
                <div className="w-full md:w-1/2 p-6 bg-white shadow-lg rounded-lg">
                    <div className="text-gray-700">
                        <LuckyGridPage handleRefresh={handleRefresh}/>
                    </div>
                </div>
            </div>

            <div className="flex items-center space-x-4">
                <StrategyRuleWeightButton refresh={refresh}/>
            </div>

            {/* 底部文案 */}
            <footer className="text-gray-600 text-center my-8" style={{color: "white"}}>
                本项目为 星球「码农会锁」第8个实战项目 <a href='https://gaga.plus'
                                                        target='_blank'>https://gaga.plus</a> @小傅哥
            </footer>
        </div>
    );
}
