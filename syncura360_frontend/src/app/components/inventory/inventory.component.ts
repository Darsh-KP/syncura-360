import { Component, OnInit, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { inventoryService, inventory, inventoryUpdateDto, inventoryUpdateRequest } from '../../services/inventory.service';
import { AgGridModule } from 'ag-grid-angular';
import { ColDef } from 'ag-grid-community';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { ItemFormComponent } from '../item-form/item-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AgGridModule,
    NavbarComponent,
    MatButtonModule,
  ],
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.css']
})
export class InventoryComponent implements OnInit {
  private dialog = inject(MatDialog);
  private inventorySvc = inject(inventoryService);

  inventoryList: inventory[] = [];
  gridApi: any;
  public themeClass: string = 'ag-theme-alpine';

  editedQuantities: { [ndc: number]: number } = {};

  errorMessage = '';
  successMessage = '';

  columnDefs: ColDef[] = [
    { headerName: 'NDC', field: 'ndc', flex: 1 },
    { headerName: 'Name', field: 'name', flex: 1 },
    { headerName: 'Pkg-Quantity', field: 'ppq', flex: 1 },
    { headerName: 'Quantity', field: 'quantity', flex: 1 },
    { headerName: 'Price', field: 'price', flex: 1 },
    {
      headerName: 'Actions',
      field: 'actions',
      flex: 1,
      suppressSizeToFit: false,
      cellRenderer: (params: any) => {
        return `
          <button class="edit-btn bg-blue-500 text-white px-2 py-1 rounded mr-4 hover:bg-blue-600">
            Edit
          </button>
          <button class="delete-btn bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600">
            Delete
          </button>
        `;
      },
      onCellClicked: (params: any) => {
        const targetClass = params.event.target.className;

        if (targetClass.includes('edit-btn')) {
          this.openItemForm(params.data);
        } else if (targetClass.includes('delete-btn')) {
          this.onDelete(params.data);
        }
      }
    }
  ];

  ngOnInit(): void {
    this.loadInventory();
  }

  loadInventory(): void {
    this.inventorySvc.getInventory().subscribe({
      next: data => {
        if (data && data.length > 0) {
          this.inventoryList = [...data]; // keep this if you're using rowData binding too
          if (this.gridApi) {

            this.gridApi.setRowData(this.inventoryList); // ✅ this will 100% force UI refresh
          }          this.errorMessage = ''; // Clear any previous error
        } else {
          // Do nothing — or show a message if you want
          this.errorMessage = '';
        }
      },
      error: err => {
        this.errorMessage = 'Failed to load inventory.';
      }
    });
  }


  openItemForm(item?: inventory): void {
    const dialogRef = this.dialog.open(ItemFormComponent, {
      width: '500px',
      data: item || null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadInventory();
    });
  }


  onDelete(item: inventory): void {
    if (!item || !item.ndc) return;

    const confirmDelete = confirm(`Are you sure you want to delete ${item.name}?`);
    if (!confirmDelete) return;


    this.inventorySvc.deleteInventory(item).subscribe({
      next: () => {
        this.successMessage = `${item.name} deleted successfully.`;
        this.loadInventory();
        this.gridApi.setRowData(this.inventoryList);
      },
      error: (err) => {
        console.error('Error deleting inventory item:', err);
        this.errorMessage = err.message || 'Failed to delete inventory item.';
      }
    });
  }

  onGridReady(params: any) {
    this.gridApi = params.api;
  }

  onQuickFilterChanged(event: any) {
    const value = event.target.value;
    this.gridApi.setQuickFilter(value);
  }
}
