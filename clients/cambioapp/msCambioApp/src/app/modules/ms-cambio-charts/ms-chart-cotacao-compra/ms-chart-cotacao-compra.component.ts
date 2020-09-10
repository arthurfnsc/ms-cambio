import { Component, OnInit } from '@angular/core';
import { MsChartComponent } from '../ms-cambio.chart';

@Component({
  selector: 'ms-chart-cotacao-compra',
  templateUrl: './ms-chart-cotacao-compra.component.html',
  styleUrls: ['./ms-chart-cotacao-compra.component.css']
})

export class MsChartCotacaoCompraComponent extends MsChartComponent {
  
  constructor() { 
    super();
  }

  ngOnInit(): void {
  }

  public DrawChart() {
    this.NewChart({
      type: 'line',
      data: {
          labels: this.Labels,
          datasets: this.CreateDataSet(),
      },
      options: {
          legend: { display: this.ShowLegend },
          elements: {
              line: { tension: 0, }
          },
          scales: {
              xAxes: [{
                  ticks: { display: this.ShowScaleX }
              }]
          }
      }
  });
  }
  public CreateDataSet() {
    let DataSets = Array();
        this.Data.forEach(data => {
            DataSets.push({
                label: data.label,
                data: data.data,
                backgroundColor: this.Color,
                borderColor: this.Color,
                borderWidth: 1,
                fill: false
            });
        });
        return DataSets;
  }
}
