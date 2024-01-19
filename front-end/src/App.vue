<template>
  <v-card>
      <v-sheet v-if="productCount!=null"
    class="d-flex align-center justify-center flex-wrap text-center mx-auto px-4"
    elevation="20"
    height="50"
    rounded
    max-width="800"
    width="100%"
  >
    <div>
      <h2 class="text-h4 font-weight-black text-orange">NUMBER OF PRODUCTS: {{productCount}}</h2>
    </div>
  </v-sheet>

    <v-layout>
    <div @click="handleCanvasClick" id="konva-container"></div>
      <v-navigation-drawer
        v-model="drawer"
        :rail="rail"
        permanent
        app
      >
        <v-list density="compact" nav>
          <v-tooltip v-for="item in menuItems" :key="item.value" :label="item.title" position="right">
            <template v-slot:activator="{ props }">
              <v-list-item v-bind="props" :active="isItemActive(item.value)" :prepend-icon="item.icon" :title="item.title" :value="item.value" @click="handleMenuItemClick(item)" >
              </v-list-item>
            </template>
            <span>{{item.title}}</span>
          </v-tooltip>
        </v-list>
      </v-navigation-drawer>
    </v-layout>
  </v-card>
</template>

<script>
import Konva from 'konva';

export default {
  data() {
    return {
      drawer: true,
      rail: true,
      menuItems: [
        { title: 'Queue', value: 'queue', icon: 'mdi-crosshairs-question' },
        { title: 'Machine', value: 'machine', icon: 'mdi-router-network' },
        { title: 'Connect', value: 'connect', icon: 'mdi-arrow-top-right' },
        { title: 'Play', value: 'play', icon: 'mdi-play-circle' },
        { title: 'Increase Product', value: 'add', icon: 'mdi-plus-circle' },
        { title: 'Decrease Product', value: 'minus', icon: 'mdi-minus-circle' },
        { title: 'Reload', value: 'reload', icon: 'mdi-reload' },
      ],
      stage: null,
      layer: null,
      selectedShape: null,
      startShape: null,
      endShape: null,
      lastshapeclicked: null,
      productCount: null
    };
  },
  methods: {
    isItemActive(value) {
      // return value !== 'add' && value !== 'minus';
      if (value === 'add' || value === 'minus') {
        return false;
      }
      return null
    },
    handleMenuItemClick(item) {
      if (item.value === this.selectedShape && item.value != 'add' && item.value != 'minus') {
        this.selectedShape = null;
      }
      else{
        this.selectedShape = item.value;
      }
      // Clear start and end shapes when a menu item is clicked
      this.startShape = null;
      this.endShape = null;
      this.lastshapeclicked = null;
      if (this.selectedShape === 'add') {
        this.addProduct();
      }
      else if(this.selectedShape === 'minus'){
        this.removeProduct();
      }
    },
    addProduct(){
      this.productCount++;
    },
    removeProduct(){
      if (this.productCount > 0) {
        this.productCount--;
      }
    },
    handleCanvasClick(event) {
      const container = this.$el.querySelector('#konva-container');
      const rect = container.getBoundingClientRect();
      const clickX = event.clientX - rect.left;
      const clickY = event.clientY - rect.top;

      if (this.selectedShape === 'connect') {
        this.handleConnectClick(clickX, clickY);
      } else if (this.selectedShape === 'queue') {
        this.drawCircle(clickX, clickY);
      } else if (this.selectedShape === 'machine') {
        this.drawSquare(clickX, clickY);
      }
    },
    drawCircle(x, y) {
      //request backend and get the queueID and put it below text
      const circleID = 0; // replace it with the return data from backend
      const circle = new Konva.Circle({
        x,
        y,
        radius: 25,
        fill: 'orange',
        id: circleID, // will be used to identify the circle later 
        draggable: false,
      });

      circle.on('click', () => {
        this.handleShapeClick(circle);
      });

      this.layer.add(circle);
      const text = new Konva.Text({
        x,
        y, 
        text: 'Q' + circleID,
        fontSize: 12,
        fill: 'black',
        width: 40, 
        align: 'center',
      });

      text.offsetX(text.width() / 2);
      text.offsetY(text.height() / 2);
      text.on('click', () => {
        this.handleShapeClick(circle);
      });

      this.layer.add(text);
      this.stage.draw();
    },

    drawSquare(x, y) {
      //request backend and get the machineID and put it below text
      const squareID = 1; // replace it with the return data from backend
      const square = new Konva.Rect({
        x,
        y,
        width: 50,
        height: 50,
        id: squareID, // will be used to identify the square later 

        fill: 'grey',
        draggable: false,
      });

      square.on('click', () => {
        this.handleShapeClick(square);
      });

      this.layer.add(square);
      const text = new Konva.Text({
        x: x + 25, 
        y: y + 25, 
        text: 'M' + squareID,
        fontSize: 12,
        fill: 'white',
        width: 40, 
        align: 'center',
      });

      text.offsetX(text.width() / 2);
      text.offsetY(text.height() / 2);
      text.on('click', () => {
        this.handleShapeClick(square);
      });

      this.layer.add(text);
      this.stage.draw();
    },

    handleShapeClick(shape) {
      console.log('Shape clicked');
      this.lastshapeclicked = shape;
    },
    handleConnectClick(clickX, clickY) {
      console.log(this.lastshapeclicked);
      const clickedShape = this.lastshapeclicked;
      if (clickedShape) {
        if (!this.startShape) {
          this.startShape = clickedShape;
        } else {
          this.endShape = clickedShape;
          this.connectShapes();
          this.startShape = null;
          this.endShape = null;
          this.lastshapeclicked = null;
        }
      }
    },
    connectShapes() {
      if (this.startShape && this.endShape && this.startShape !== this.endShape) {
        const dx = this.endShape.x() - this.startShape.x();
        const dy = this.endShape.y() - this.startShape.y();
        let angle = Math.atan2(-dy, dx);

        const radius = 48;
        let from = {
          x: this.startShape.x(),
          y: this.startShape.y(),
        };

        let to = {
          x: this.endShape.x(),
          y: this.endShape.y(),
        };

        if (this.startShape.getClassName() === 'Rect') {
          from.x += 25;
          from.y += 25;
        }

        if (this.endShape.getClassName() === 'Rect') {
          to.x += 25;
          to.y += 25;
        }

        const arrow = new Konva.Arrow({
          points: [
            from.x + -radius * Math.cos(angle + Math.PI),
            from.y + radius * Math.sin(angle + Math.PI),
            to.x + -radius * Math.cos(angle),
            to.y + radius * Math.sin(angle),
          ],
          fill: 'black',
          stroke: 'black',
          strokeWidth: 2,
        });

        this.layer.add(arrow);
        this.stage.draw();
      }
  },

  },
  mounted() {
    this.stage = new Konva.Stage({
      container: 'konva-container',
      width: window.innerWidth,
      height: window.innerHeight,
    });

    this.layer = new Konva.Layer();
    this.stage.add(this.layer);
  },
};
</script>


<style scoped>
</style>
