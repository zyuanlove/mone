/*
 * Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

let config = (function () {
  return function (that, myFullShow) {
    return {
      tooltip: {
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
      },
      color: ['#f1a86f'],
      title: [{
        text: '每月部署次数统计',
        // subtext: '总计 ' + builderJson.all,
        left: '12%',
        top: '5%',
        textAlign: 'center',
        textStyle: {
          // 文字颜色
          color: 'black',
          fontSize: 16
        }
      }],
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        name: '',
        type: 'category',
        splitLine: { show: false }, // 去除网格线
        axisLine: {
          lineStyle: {
            color: '#aeb4b7fa'
          },
          width: 1
        },
        axisLabel: {
          textStyle: {
            color: 'black'
          }
        },
        data: that.deployTimesPerMonth.map(it => it.time)
      },
      yAxis: {
        name: '发布次数（次）',
        nameTextStyle: {
          padding: [0, 0, 30, 0], // 四个数字分别为上右下左与原位置距离
          fontWeight: 'bold',
          fontSize: 14,
          color: '#919191'
        },
        nameRotate: 90,
        nameLocation: 'middle',
        type: 'value',
        splitLine: { show: false }, // 去除网格线
        // axisLine: {
        //   show: false
        // },
        axisTick: {
          show: false
        },
        z: 10
      },
      series: [
        {
          type: 'bar',
          stack: '总量',
          //   label: {
          //     show: true,
          //     position: 'top'
          //   },
          data: that.deployTimesPerMonth.map(it => it.deployTimes)
        }
      ]
    }
  }
})()

export default config
