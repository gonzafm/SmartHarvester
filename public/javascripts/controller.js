/**
 * Created by Gonza on 2/18/14.
 */
function HarvestingStatus($scope, $http) {
    $scope.loadData = function () {
        $http.get('status').
            success(function (data) {
                $scope.harvestingStatus = data;
            }).error(function (data) {
                console.log(data)
            })
    }
    $scope.loadData()
    while ($scope.harvestingStatus != undefined
            && $scope.harvestingStatus.isComplete != true) {
        $scope.loadData()
    }
}