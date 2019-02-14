// float inefficiency_cost(int target_speed, int intended_lane, int final_lane, vector<int> lane_speeds) {
//     /*
//     Cost becomes higher for trajectories with intended lane and final lane that have traffic slower than target_speed.
//     */

//     float speed_intended = lane_speeds[intended_lane];
//     float speed_final = lane_speeds[final_lane];
//     float cost = (2.0*target_speed - speed_intended - speed_final)/target_speed;
//     return cost;
// }

