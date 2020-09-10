import { ViewChild, OnInit, OnChanges, SimpleChanges, Input } from '@angular/core';
import { Chart } from 'chart.js';

export abstract class MsChartComponent implements OnInit, OnChanges {
    private _canvas;
    private _chart: Chart;
  
    @Input() public Data: any[];
    @Input() public Labels: any[];
    @Input() public ShowLegend: boolean;
    @Input() public ShowScaleX: boolean;
    @Input() public Color: string;
  
    @ViewChild('stchart', { static: true }) _containerChart;
  
    ngOnInit(): void { }
  
    public abstract DrawChart();
    public abstract CreateDataSet();
  
    public NewChart(Properties) {
      this.InjectDependencies();
      if (this._chart != undefined)
        this._chart = undefined;
      this._chart = new Chart(this._canvas.getContext("2d"), Properties);
    };
  
    public InjectDependencies() {
      if (this._canvas === undefined) {
        this._canvas = document.createElement('canvas');
        this._containerChart.nativeElement.appendChild(this._canvas);
      }
    }
  
    ngOnChanges(changes: SimpleChanges): void {
      this.DrawChart();
    }
  }