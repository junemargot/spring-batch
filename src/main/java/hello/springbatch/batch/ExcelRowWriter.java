package hello.springbatch.batch;

import hello.springbatch.entity.BeforeEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelRowWriter implements ItemStreamWriter<BeforeEntity> {

  private final String filePath;
  private Workbook workbook;
  private Sheet sheet;
  private int currentRowNumber;

  public ExcelRowWriter(String filePath) {
    this.filePath = filePath;
    this.currentRowNumber = 0;
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet("Sheet1");
  }

  @Override
  public void write(Chunk<? extends BeforeEntity> chunk) {
    for(BeforeEntity beforeEntity : chunk) {
      Row row = sheet.createRow(currentRowNumber++);
      row.createCell(0).setCellValue(beforeEntity.getUsername());
    }
  }

  @Override
  public void close() throws ItemStreamException {
    try(FileOutputStream fos = new FileOutputStream(filePath)) {
      workbook.write(fos);

    } catch(IOException e) {
      throw new ItemStreamException(e);

    } finally {
      try {
        workbook.close();

      } catch(IOException e) {
        throw new ItemStreamException(e);
      }
    }
  }
}
